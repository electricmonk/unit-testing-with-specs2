package com.wixpress

/**
 * @author shaiyallin
 * @since 8/12/13
 */

trait RegistrationModule { this: UserModule with CryptographyModule =>

  trait UserRegistrar {
    def register(request: RegistrationRequest) {
      val user = User(request.email, digester.digest(request.password))
      userDao.insert(user)
    }
  }

  case class RegistrationRequest(email: String, password: String)
}

trait UserModule {

  val userDao: UserDao

  trait UserDao {
    def insert(user: User)
  }

  case class User(email: String, digestedPassword: String)

  case class DuplicateUserEmailException(email: String) extends RuntimeException
}

trait CryptographyModule {

  val digester: PasswordDigester

  trait PasswordDigester {
    def digest(password: String): String
  }
}