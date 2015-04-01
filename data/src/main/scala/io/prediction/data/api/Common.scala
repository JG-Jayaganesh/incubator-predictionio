/** Copyright 2015 TappingStone, Inc.
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

package io.prediction.data.api

import io.prediction.data.webhooks.ConnectorException

import spray.routing._
import spray.routing.Directives._
import spray.http.StatusCodes
import spray.http.StatusCode
import spray.httpx.Json4sSupport

import org.json4s.Formats
import org.json4s.DefaultFormats

object Common {

  object Json4sProtocol extends Json4sSupport {
    implicit def json4sFormats: Formats = DefaultFormats
  }

  import Json4sProtocol._

  val rejectionHandler = RejectionHandler {
    case MalformedRequestContentRejection(msg, _) :: _ =>
      complete(StatusCodes.BadRequest, Map("message" -> msg))
    case MissingQueryParamRejection(msg) :: _ =>
      complete(StatusCodes.NotFound,
        Map("message" -> s"missing required query parameter ${msg}."))
    case AuthenticationFailedRejection(cause, challengeHeaders) :: _ =>
      complete(StatusCodes.Unauthorized, challengeHeaders,
        Map("message" -> s"Invalid accessKey."))
  }

  val exceptionHandler = ExceptionHandler {
    case e: ConnectorException => {
      val msg = s"${e.getMessage()}"
      complete(StatusCodes.BadRequest, Map("message" -> msg))
    }
    case e: Exception => {
      val msg = s"${e.getMessage()}"
      complete(StatusCodes.BadRequest, Map("message" -> msg))
    }
  }
}
