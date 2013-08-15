package com.wixpress

import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

/**
 * This test adds support traits abstracting away mocking of collaborators
 *
 * @author shaiyallin
 * @since 8/12/13
 */

class RegistrationTest4 extends SpecificationWithJUnit with Mockito {

  trait Context extends Scope with RegistrationModule
    with UserModuleSupport with CryptographyModuleSupport {

    val registrar = new UserRegistrar {}

    val email = "me@my.org"
  }

  "user registration" should {
    "insert a user into the database" in new Context {
      val request = RegistrationRequest(email = email, password = "")

      registrar.register(request)

      there was one(userDao).insert(User(email = email, digestedPassword = null))
    }

    "digest the user's password prior to inserting the user" in new Context {
      val password = "1234"
      val digestedPassword = "abcd"
      digester.digest(password) returns digestedPassword

      registrar.register(RegistrationRequest(email = email, password = password))

      there was one(userDao).insert(User(email = email, digestedPassword = digestedPassword))
    }

    "fail when a user with the specified email already exists" in new Context {
      userDao.insert(User(email = email, digestedPassword = null)) throws
        new DuplicateUserEmailException(email)

      registrar.register(
        RegistrationRequest(email = email, password = "")) must
          throwA[DuplicateUserEmailException]
    }
  }
}