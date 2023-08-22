/*
 * Copyright ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package net.consensys.linea.zktracer.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import net.consensys.linea.zktracer.AbstractModuleCorsetTest;
import net.consensys.linea.zktracer.module.ext.Ext;
import net.consensys.linea.zktracer.opcode.OpCode;
import net.consensys.linea.zktracer.opcode.OpCodeData;
import org.apache.tuweni.bytes.Bytes32;
import org.apache.tuweni.units.bigints.UInt256;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExtTracerTest extends AbstractModuleCorsetTest {
  static final Random rand = new Random();

  @Override
  public Stream<Arguments> provideRandomArguments() {
    final List<Arguments> arguments = new ArrayList<>();
    for (OpCode opCode : getModuleTracer().supportedOpCodes()) {
      for (int i = 0; i <= 16; i++) {
        arguments.add(
            Arguments.of(
                opCode.getData(),
                List.of(Bytes32.random(rand), Bytes32.random(rand), Bytes32.random(rand))));
      }
    }

    return arguments.stream();
  }

  @Override
  public Stream<Arguments> provideNonRandomArguments() {
    List<Arguments> arguments = new ArrayList<>();
    for (OpCode opCode : getModuleTracer().supportedOpCodes()) {
      for (int k = 1; k <= 4; k++) {
        for (int i = 1; i <= 4; i++) {
          arguments.add(
              Arguments.of(
                  opCode.getData(),
                  List.of(UInt256.valueOf(i), UInt256.valueOf(k), UInt256.valueOf(k))));
        }
      }
    }

    return arguments.stream();
  }

  @ParameterizedTest()
  @MethodSource("provideZeroValueTest")
  public void argumentZeroValueTestMulModTest(
      final OpCodeData opCodeData, final List<Bytes32> arguments) {
    runTest(opCodeData, arguments);
  }

  @ParameterizedTest()
  @MethodSource("provideModulusZeroValueArguments")
  public void modulusZeroValueTestMulModTest(
      final OpCodeData opCodeData, final List<Bytes32> arguments) {
    Assertions.assertThrows(ArithmeticException.class, () -> runTest(opCodeData, arguments));
  }

  @ParameterizedTest()
  @MethodSource("provideTinyValueArguments")
  public void tinyValueTest(final OpCodeData opCode, final List<Bytes32> arguments) {
    runTest(opCode, arguments);
  }

  @ParameterizedTest()
  @MethodSource("provideMaxValueArguments")
  public void maxValueTest(final OpCodeData opCode, final List<Bytes32> arguments) {
    runTest(opCode, arguments);
  }

  @Override
  protected Module getModuleTracer() {
    return new Ext();
  }

  public Stream<Arguments> provideZeroValueTest() {
    List<Arguments> arguments = new ArrayList<>();
    for (OpCode opCode : getModuleTracer().supportedOpCodes()) {
      arguments.add(
          Arguments.of(
              opCode.getData(),
              List.of(UInt256.valueOf(0), UInt256.valueOf(12), UInt256.valueOf(6))));
    }

    return arguments.stream();
  }

  public Stream<Arguments> provideModulusZeroValueArguments() {
    List<Arguments> arguments = new ArrayList<>();
    for (OpCode opCode : getModuleTracer().supportedOpCodes()) {
      arguments.add(
          Arguments.of(
              opCode.getData(),
              List.of(UInt256.valueOf(1), UInt256.valueOf(1), UInt256.valueOf(0))));
    }

    return arguments.stream();
  }

  public Stream<Arguments> provideTinyValueArguments() {
    List<Arguments> arguments = new ArrayList<>();
    for (OpCode opCode : getModuleTracer().supportedOpCodes()) {
      arguments.add(
          Arguments.of(
              opCode.getData(),
              List.of(UInt256.valueOf(6), UInt256.valueOf(7), UInt256.valueOf(13))));
    }

    return arguments.stream();
  }

  public Stream<Arguments> provideMaxValueArguments() {
    List<Arguments> arguments = new ArrayList<>();
    for (OpCode opCode : getModuleTracer().supportedOpCodes()) {
      arguments.add(
          Arguments.of(
              opCode.getData(), List.of(UInt256.MAX_VALUE, UInt256.MAX_VALUE, UInt256.MAX_VALUE)));
    }

    return arguments.stream();
  }
}
