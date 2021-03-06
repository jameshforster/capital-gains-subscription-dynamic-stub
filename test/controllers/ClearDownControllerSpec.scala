/*
 * Copyright 2017 HM Revenue & Customs
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

package controllers

import org.scalatest.mock.MockitoSugar
import repositories._
import uk.gov.hmrc.play.microservice.controller.BaseController
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class ClearDownControllerSpec extends UnitSpec with BaseController with WithFakeApplication with MockitoSugar {

  private val successful = Ok("Success")
  private val failed = BadRequest("Could not delete data")

  def setupController(): ClearDownController = {

    val mockRegistrationConnector = mock[BusinessPartnerRepository]
    val mockSubscriptionConnector = mock[SubscriptionRepository]
    val mockEnrolmentConnector = mock[TaxEnrolmentSubscriberRepository]
    val mockAgentRelationshipConnector = mock[AgentClientRelationshipRepository]
    val mockExceptionsRepository = mock[RouteExceptionRepository]
    val mockSchemaRepository = mock[SchemaRepository]

    new ClearDownController(mockRegistrationConnector, mockSubscriptionConnector,
      mockEnrolmentConnector, mockAgentRelationshipConnector, mockExceptionsRepository, mockSchemaRepository)
  }

  "Calling .checkForFailed" should {

    val controller = setupController()
    val successfulClearDown = Seq(successful, successful, successful, successful)
    val oneFailedClearDown = Seq(successful, failed, successful, successful)
    val twoFailedClearDowns = Seq(failed, successful, successful, failed)
    val threeFailedClearDowns = Seq(failed, failed, successful, failed)
    val fourFailedClearDowns = Seq(failed, failed, failed, failed)

    "when all the clears succeed" in {
      await(controller.checkSuccess(successfulClearDown)) shouldEqual true
    }

    "when one of the clears fails" in {
      await(controller.checkSuccess(oneFailedClearDown)) shouldEqual false
    }

    "when two of the clears fail" in {
      await(controller.checkSuccess(twoFailedClearDowns)) shouldEqual false
    }

    "when three of the clears fail" in {
      await(controller.checkSuccess(threeFailedClearDowns)) shouldEqual false
    }

    "when all four of the clears fail" in {
      await(controller.checkSuccess(fourFailedClearDowns)) shouldEqual false
    }
  }
}
