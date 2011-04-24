package org.plovr;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.plovr.io.Files;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * {@link ConfigParser} extracts a {@link Config} from a JSON config file.
 *
 * @author bolinfest@gmail.com (Michael Bolin)
 */
public final class ConfigParser {

  /** Utility class; do not instantiate. */
  private ConfigParser() {}

  public static Config parseFile(File file) throws IOException {
    JsonParser jsonParser = new JsonParser();
    String rootConfigFileContent = Files.toString(file);
    JsonElement root = jsonParser.parse(rootConfigFileContent);

    Preconditions.checkNotNull(root);
    Preconditions.checkArgument(root.isJsonObject());

    File parentDirectory = file.getAbsoluteFile().getParentFile();
    Config.Builder builder = Config.builder(parentDirectory, file,
        rootConfigFileContent);

    // Keep track of the keys in the options object so that plovr can warn
    // about unused values in the config file.
    JsonObject map = root.getAsJsonObject();
    Set<String> options = Sets.newHashSet();
    for (Map.Entry<String, JsonElement> entry : map.entrySet()) {
      options.add(entry.getKey());
    }

    // Loop over the options in enum value order because it is helpful if some
    // option values are guaranteed to be processed before others.
    for (ConfigOption option : ConfigOption.values()) {
      String optionName = option.getName();
      if (!map.has(option.getName())) continue;

      JsonElement element = map.get(optionName);
      option.update(builder, element);
      options.remove(optionName);
    }

    for (String unusedOption : options) {
      System.err.printf("WARNING: UNUSED OPTION '%s' in %s. " +
          "See %s for the complete list of options.\n",
          unusedOption,
          file.getAbsolutePath(),
          "http://plovr.com/options.html");
    }

    return builder.build();
  }

  public static Config update(Config config, QueryData queryData) {
    Config.Builder builder = Config.builder(config);
    for (ConfigOption option : ConfigOption.values()) {
      option.update(builder, queryData);
    }
    return builder.build();
  }
}