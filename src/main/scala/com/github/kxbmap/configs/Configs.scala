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

package com.github.kxbmap.configs

import com.typesafe.config.{Config, ConfigException, ConfigMemorySize}
import java.io.File
import java.net.{InetAddress, InetSocketAddress, UnknownHostException}
import java.nio.file.{Path, Paths}
import java.time.{Duration => JDuration}
import java.util.concurrent.TimeUnit
import scala.annotation.implicitNotFound
import scala.collection.JavaConversions._
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.reflect.{ClassTag, classTag}
import scala.util.Try


@implicitNotFound("No implicit Configs defined for ${T}.")
trait Configs[T] {

  def extract(config: Config): T

  def map[U](f: T => U): Configs[U] = f.compose(extract)(_)

  def orElse[U >: T](other: Configs[U]): Configs[U] = c =>
    try
      extract(c)
    catch {
      case _: ConfigException =>
        other.extract(c)
    }
}

object Configs extends ConfigsInstances {

  @inline def apply[T](implicit C: Configs[T]): Configs[T] = C

  def configs[T](f: Config => T): Configs[T] = f(_)

  def atPath[T](f: (Config, String) => T): AtPath[T] = c => f(c, _)
}

trait ConfigsInstances {

  implicit def materializeConfigs[T]: Configs[T] = macro macros.ConfigsMacro.materialize[T]


  implicit lazy val configConfigs: Configs[Config] = identity


  implicit lazy val intAtPath: AtPath[Int] = _.getInt

  implicit def intsAtPath[C[_]](implicit cbf: CBF[C, Int]): AtPath[C[Int]] = c =>
    c.getIntList(_).map(_.toInt)(collection.breakOut)

  implicit lazy val longAtPath: AtPath[Long] = _.getLong

  implicit def longsAtPath[C[_]](implicit cbf: CBF[C, Long]): AtPath[C[Long]] = c =>
    c.getLongList(_).map(_.toLong)(collection.breakOut)

  implicit lazy val doubleAtPath: AtPath[Double] = _.getDouble

  implicit def doublesAtPath[C[_]](implicit cbf: CBF[C, Double]): AtPath[C[Double]] = c =>
    c.getDoubleList(_).map(_.toDouble)(collection.breakOut)

  implicit lazy val booleanAtPath: AtPath[Boolean] = _.getBoolean

  implicit def booleansAtPath[C[_]](implicit cbf: CBF[C, Boolean]): AtPath[C[Boolean]] = c =>
    c.getBooleanList(_).map(_.booleanValue())(collection.breakOut)

  implicit lazy val stringAtPath: AtPath[String] = _.getString

  implicit def stringsAtPath[C[_]](implicit cbf: CBF[C, String]): AtPath[C[String]] = c => c.getStringList(_).to[C]


  def mapConfigs[K, T: AtPath](f: String => K): Configs[Map[K, T]] = c =>
    c.root().keysIterator.map(k => f(k) -> c.get[T](k)).toMap

  implicit def stringMapConfigs[T: AtPath]: Configs[Map[String, T]] = mapConfigs(identity)

  implicit def symbolMapConfigs[T: AtPath]: Configs[Map[Symbol, T]] = mapConfigs(Symbol.apply)


  implicit lazy val symbolAtPath: AtPath[Symbol] = AtPath.by(Symbol.apply)

  implicit def symbolsAtPath[C[_]](implicit cbf: CBF[C, Symbol]): AtPath[C[Symbol]] = AtPath.listBy(Symbol.apply)


  implicit def configsAtPath[T: Configs]: AtPath[T] = c => c.getConfig(_).extract[T]

  implicit def configsCollectionAtPath[C[_], T: Configs](implicit cbf: CBF[C, T]): AtPath[C[T]] = c =>
    c.getConfigList(_).map(_.extract[T])(collection.breakOut)


  implicit def optionConfigs[T: Configs]: Configs[Option[T]] = c => Some(c.extract[T])

  implicit def optionAtPath[T: AtPath]: AtPath[Option[T]] = c => p =>
    if (c.hasPathOrNull(p) && !c.getIsNull(p)) Some(c.get[T](p)) else None


  implicit def eitherConfigs[E <: Throwable : ClassTag, T: Configs]: Configs[Either[E, T]] = c => eitherFrom(c.extract[T])

  implicit def eitherAtPath[E <: Throwable : ClassTag, T: AtPath]: AtPath[Either[E, T]] = c => p => eitherFrom(c.get[T](p))

  private def eitherFrom[E <: Throwable : ClassTag, T](value: => T): Either[E, T] =
    try Right(value) catch {
      case e if classTag[E].runtimeClass.isAssignableFrom(e.getClass) =>
        Left(e.asInstanceOf[E])
    }


  implicit def tryConfigs[T: Configs]: Configs[Try[T]] = c => Try(c.extract[T])

  implicit def tryAtPath[T: AtPath]: AtPath[Try[T]] = c => p => Try(c.get[T](p))


  implicit lazy val finiteDurationAtPath: AtPath[FiniteDuration] = c => p =>
    Duration.fromNanos(c.getDuration(p, TimeUnit.NANOSECONDS))

  implicit lazy val durationAtPath: AtPath[Duration] = finiteDurationAtPath.asInstanceOf[AtPath[Duration]]

  implicit def durationsAtPath[C[_], D >: FiniteDuration <: Duration](implicit cbf: CBF[C, D]): AtPath[C[D]] = c =>
    c.getDurationList(_, TimeUnit.NANOSECONDS).map(Duration.fromNanos(_))(collection.breakOut)


  implicit lazy val javaTimeDurationAtPath: AtPath[JDuration] = _.getDuration

  implicit def javaTimeDurationsAtPath[C[_]](implicit cbf: CBF[C, JDuration]): AtPath[C[JDuration]] = c =>
    c.getDurationList(_).to[C]

  implicit lazy val configMemorySizeAtPath: AtPath[ConfigMemorySize] = _.getMemorySize

  implicit def configMemorySizesAtPath[C[_]](implicit cbf: CBF[C, ConfigMemorySize]): AtPath[C[ConfigMemorySize]] = c =>
    c.getMemorySizeList(_).to[C]


  implicit lazy val fileAtPath: AtPath[File] = AtPath.by(new File(_: String))

  implicit def filesAtPath[C[_]](implicit cbf: CBF[C, File]): AtPath[C[File]] = AtPath.listBy(new File(_: String))


  implicit lazy val pathAtPath: AtPath[Path] = AtPath.by(Paths.get(_: String))

  implicit def pathsAtPath[C[_]](implicit cbf: CBF[C, Path]): AtPath[C[Path]] = AtPath.listBy(Paths.get(_: String))


  implicit lazy val inetAddressAtPath: AtPath[InetAddress] = c => p =>
    try
      InetAddress.getByName(c.get[String](p))
    catch {
      case e: UnknownHostException =>
        throw new ConfigException.BadValue(c.origin(), p, e.getMessage, e)
    }

  implicit def inetAddressesAtPath[C[_]](implicit cbf: CBF[C, InetAddress]): AtPath[C[InetAddress]] = c => p =>
    try
      c.get[Seq[String]](p).map(InetAddress.getByName)(collection.breakOut)
    catch {
      case e: UnknownHostException =>
        throw new ConfigException.BadValue(c.origin(), p, e.getMessage, e)
    }


  implicit lazy val inetSocketAddressConfigs: Configs[InetSocketAddress] = c => {
    val port = c.getInt("port")
    c.opt[String]("hostname").fold {
      new InetSocketAddress(c.get[InetAddress]("addr"), port)
    } {
      hostname => new InetSocketAddress(hostname, port)
    }
  }

}
