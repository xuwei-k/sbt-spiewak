/*
 * Copyright 2019 Daniel Spiewak
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

package sbtspiewak

import sbt._, Keys._

import bintray.BintrayKeys._
import sbttravisci.TravisCiPlugin, TravisCiPlugin.autoImport._

object SpiewakBintrayPlugin extends AutoPlugin {

  override def requires = SpiewakPlugin && TravisCiPlugin && _root_.bintray.BintrayPlugin

  override def trigger = allRequirements

  override def buildSettings =
    addCommandAlias("release", "; reload; project /; +mimaReportBinaryIssues; +bintrayEnsureBintrayPackageExists; +publish; +bintrayRelease") ++
    Seq(
      bintray / credentials := {
        val old = (bintray / credentials).value

        if (isTravisBuild.value) Nil else old
      },

      bintrayReleaseOnPublish := false)

  override def projectSettings =
    Seq(
      bintrayRelease := Def.taskDyn {
        val default = bintrayRelease.taskValue
        if ((publish / skip).?.value.getOrElse(false))
          Def.task(())
        else
          Def.task(default.value)
      }.value)
}
