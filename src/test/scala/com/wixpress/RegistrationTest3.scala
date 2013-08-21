package com.wixpress

import org.specs2.mutable.SpecificationWithJUnit
import org.mockito.Mockito._
import org.specs2.mock.Mockito
import org.specs2.specification.BeforeExample

/**
 * This test is an almost direct translation of the JUnit test into Specs2 syntax
 *
 * @author shaiyallin
 * @since 8/12/13
 */

class RegistrationTest3 extends SpecificationWithJUnit with Mockito with BeforeExample
  with RegistrationModule with UserModule with CryptographyModule {

  val userDao = mock[UserDao]
  val digester = mock[PasswordDigester]
  val registrar = new UserRegistrar {}

  val email = "me@my.org"

  def before {
    reset(userDao, digester)
  }

  sequential

  "user registration" should {
    "insert a user into the database" in {
      registrar.register(RegistrationRequest(email = email, password = ""))

      there was one(userDao).insert(User(email = email, digestedPassword = null))
    }

    "digest the user's password prior to inserting the user" in {
      val password = "1234"
      val digestedPassword = "4321"
      digester.digest(password) returns digestedPassword

      registrar.register(RegistrationRequest(email = email, password = password))

      there was one(userDao).insert(User(email = email, digestedPassword = digestedPassword))
    }

    "fail when a user with the specified email already exists" in {
      userDao.insert(User(email = email, digestedPassword = null)) throws
        new DuplicateUserEmailException(email)

      registrar.register(
        RegistrationRequest(email = email, password = "")) must
          throwA[DuplicateUserEmailException]
    }
  }

}