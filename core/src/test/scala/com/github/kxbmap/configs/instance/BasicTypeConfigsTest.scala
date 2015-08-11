/*
 * Copyright 2013-2015 Tsukasa Kitachi
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

package com.github.kxbmap.configs.instance

import com.github.kxbmap.configs.ConfigProp
import java.{lang => jl, util => ju}
import scalaprops.{Properties, Scalaprops}
import scalaz.std.string._

object BasicTypeConfigsTest extends Scalaprops with ConfigProp {

  val byte = check[Byte]
  val javaByte = check[jl.Byte]
  val javaByteJList = {
    implicit val h = hideConfigs[jl.Byte]
    check[ju.List[jl.Byte]]
  }
  val byteCollections = {
    implicit val h = hideConfigs[Byte]
    Properties.list(
      check[List[Byte]].mapId("list " + _),
      check[Vector[Byte]].mapId("vector " + _),
      check[Stream[Byte]].mapId("stream " + _),
      check[Array[Byte]].mapId("array " + _)
    )
  }

  val short = check[Short]
  val javaShort = check[jl.Short]
  val javaShortList = {
    implicit val h = hideConfigs[jl.Short]
    check[ju.List[jl.Short]]
  }
  val shortCollections = {
    implicit val h = hideConfigs[Short]
    Properties.list(
      check[List[Short]].mapId("list " + _),
      check[Vector[Short]].mapId("vector " + _),
      check[Stream[Short]].mapId("stream " + _),
      check[Array[Short]].mapId("array " + _)
    )
  }

  val int = check[Int]
  val javaInteger = check[jl.Integer]
  val javaIntegerList = {
    implicit val h = hideConfigs[jl.Integer]
    check[ju.List[jl.Integer]]
  }
  val intCollections = {
    implicit val h = hideConfigs[Int]
    Properties.list(
      check[List[Int]].mapId("list " + _),
      check[Vector[Int]].mapId("vector " + _),
      check[Stream[Int]].mapId("stream " + _),
      check[Array[Int]].mapId("array " + _)
    )
  }

  val long = check[Long]
  val javaLong = check[jl.Long]
  val javaLongList = {
    implicit val h = hideConfigs[jl.Long]
    check[ju.List[jl.Long]]
  }
  val longCollections = {
    implicit val h = hideConfigs[Long]
    Properties.list(
      check[List[Long]].mapId("list " + _),
      check[Vector[Long]].mapId("vector " + _),
      check[Stream[Long]].mapId("stream " + _),
      check[Array[Long]].mapId("array " + _)
    )
  }

  val float = check[Float]
  val javaFloat = check[jl.Float]
  val javaFloatList = {
    implicit val h = hideConfigs[jl.Float]
    check[ju.List[jl.Float]]
  }
  val floatCollections = {
    implicit val h = hideConfigs[Float]
    Properties.list(
      check[List[Float]].mapId("list " + _),
      check[Vector[Float]].mapId("vector " + _),
      check[Stream[Float]].mapId("stream " + _),
      check[Array[Float]].mapId("array " + _)
    )
  }

  val double = check[Double]
  val javaDouble = check[jl.Double]
  val javaDoubleList = {
    implicit val h = hideConfigs[jl.Double]
    check[ju.List[jl.Double]]
  }
  val doubleCollections = {
    implicit val h = hideConfigs[Double]
    Properties.list(
      check[List[Double]].mapId("list " + _),
      check[Vector[Double]].mapId("vector " + _),
      check[Stream[Double]].mapId("stream " + _),
      check[Array[Double]].mapId("array " + _)
    )
  }

  val boolean = check[Boolean]
  val javaBoolean = check[jl.Boolean]
  val javaBooleanList = {
    implicit val h = hideConfigs[jl.Boolean]
    check[ju.List[jl.Boolean]]
  }
  val booleanCollections = {
    implicit val h = hideConfigs[Boolean]
    Properties.list(
      check[List[Boolean]].mapId("list " + _),
      check[Vector[Boolean]].mapId("vector " + _),
      check[Stream[Boolean]].mapId("stream " + _),
      check[Array[Boolean]].mapId("array " + _)
    )
  }

  val char = check[Char]
  val javaCharacter = check[jl.Character]
  val javaCharacterList = {
    implicit val h = hideConfigs[jl.Character]
    check[ju.List[jl.Character]]
  }
  val charCollections = {
    implicit val h = hideConfigs[Char]
    Properties.list(
      check[List[Char]].mapId("list " + _),
      check[Vector[Char]].mapId("vector " + _),
      check[Stream[Char]].mapId("stream " + _),
      check[Array[Char]].mapId("array " + _)
    )
  }

  val string = check[String]
  val stringList = {
    implicit val h = hideConfigs[String]
    check[ju.List[String]]
  }
  val stringCollections = {
    implicit val h = hideConfigs[String]
    Properties.list(
      check[List[String]].mapId("list " + _),
      check[Vector[String]].mapId("vector " + _),
      check[Stream[String]].mapId("stream " + _),
      check[Array[String]].mapId("array " + _)
    )
  }

}