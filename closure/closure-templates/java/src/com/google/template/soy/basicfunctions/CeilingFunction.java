/*
 * Copyright 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.template.soy.basicfunctions;

import static com.google.template.soy.javasrc.restricted.SoyJavaSrcFunctionUtils.toIntegerJavaExpr;
import static com.google.template.soy.tofu.restricted.SoyTofuFunctionUtils.toSoyData;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.javasrc.restricted.JavaCodeUtils;
import com.google.template.soy.javasrc.restricted.JavaExpr;
import com.google.template.soy.javasrc.restricted.SoyJavaSrcFunction;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsSrcFunction;
import com.google.template.soy.tofu.restricted.SoyTofuFunction;


/**
 * Soy function that takes the ceiling of a number.
 *
 * @author Kai Huang
 */
@Singleton
class CeilingFunction implements SoyTofuFunction, SoyJsSrcFunction, SoyJavaSrcFunction {


  @Inject
  CeilingFunction() {}


  @Override public String getName() {
    return "ceiling";
  }


  @Override public boolean isValidArgsSize(int numArgs) {
    return numArgs == 1;
  }


  @Override public SoyData computeForTofu(List<SoyData> args) {
    SoyData arg = args.get(0);

    if (arg instanceof IntegerData) {
      return arg;
    } else {
      return toSoyData((int) Math.ceil(arg.floatValue()));
    }
  }


  @Override public JsExpr computeForJsSrc(List<JsExpr> args) {
    JsExpr arg = args.get(0);

    return new JsExpr("Math.ceil(" + arg.getText() + ")", Integer.MAX_VALUE);
  }


  @Override public JavaExpr computeForJavaSrc(List<JavaExpr> args) {
    JavaExpr arg = args.get(0);

    return toIntegerJavaExpr(JavaCodeUtils.genNewIntegerData(
        "(int) Math.ceil(" + JavaCodeUtils.genNumberValue(arg) + ")"));
  }

}
