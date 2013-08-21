package com.wixpress

import org.specs2.mock.mockito.MocksCreation
import org.specs2.matcher.{AlwaysMatcher, Expectable, Matcher}

/**
 * @author shaiyallin
 * @since 8/12/13
 */

trait UserModuleSupport extends UserModule with MocksCreation {
  override val userDao = mock[UserDao]

  def aUserWith(email: Matcher[String] = AlwaysMatcher(),
                password: Matcher[String] = AlwaysMatcher()): Matcher[User] =
    email ^^ {(u: User) => u.email} and
    password ^^ {(u: User) => u.digestedPassword}

}

trait CryptographyModuleSupport extends CryptographyModule with MocksCreation {
  override val digester = mock[PasswordDigester]
}