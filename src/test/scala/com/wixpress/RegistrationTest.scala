package com.wixpress

import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import org.specs2.matcher.{AlwaysMatcher, Matcher}

/**
 * @author shaiyallin
 * @since 8/12/13
 */

class RegistrationTest extends SpecificationWithJUnit with Mockito {

  trait Context extends Scope with RegistrationModule
    with UserModuleSupport with CryptographyModuleSupport {

    val registrar = new UserRegistrar {}

    def aRequestWith(email: String = "me@my.org", password: String = "123456") =
      RegistrationRequest(email, password)

  }

  "user registration" should {
    "insert a user into the database" in new Context {
      val email = "me@my.org"
      val request = aRequestWith(email = email)

      registrar.register(request)

      there was one(userDao).insert(aUserWith(
        email = equalTo(request.email))
      )
    }

    "digest the user's password prior to inserting the user" in new Context {
      val password = "1234"
      val digestedPassword = "abcd"
      digester.digest(password) returns digestedPassword

      registrar.register(aRequestWith(password = password))

      there was one(userDao).insert(aUserWith(
        password = equalTo(digestedPassword)
      ))
    }

    "fail when a user with the specified email already exists" in new Context {
      val email = "me@my.org"
      userDao.insert(argThat(aUserWith(email = equalTo(email)))) throws
        new DuplicateUserEmailException(email)

      registrar.register(
        aRequestWith(email = email)) must throwA[DuplicateUserEmailException]
    }
  }
}