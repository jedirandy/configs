/*
 * Copyright 2013 Tsukasa Kitachi
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

package com.github.kxbmap.configs

import com.typesafe.config.ConfigException
import scala.util.control.NonFatal

object Catch {

  def missing: Catch          = Implicits.missing
  def configException: Catch  = Implicits.configException
  def nonFatal: Catch         = Implicits.nonFatal

  object Implicits {
    implicit lazy val missing: Catch = {
      case _: ConfigException.Missing => true
      case _                          => false
    }

    implicit lazy val configException: Catch = {
      case _: ConfigException => true
      case _                  => false
    }

    implicit lazy val nonFatal: Catch = NonFatal.apply
  }

}
