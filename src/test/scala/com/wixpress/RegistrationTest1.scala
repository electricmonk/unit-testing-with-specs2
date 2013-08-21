package com.wixpress

import org.junit.{Before, Test}
import org.mockito.Mockito._

/**
 * This test demonstrates the most naive usage of JUnit to test a Cake Pattern-based app
 *
 * @author shaiyallin
 * @since 8/15/13
 */
class RegistrationTest1 extends RegistrationModule
    with UserModule with CryptographyModule {

  val userDao = mock(classOf[UserDao])
  val digester = mock(classOf[PasswordDigester])
  val registrar = new UserRegistrar {}

  val email = "me@my.org"
  val password = "1234"

  @Before def setup() {
    reset(userDao, digester)
  }

  @Test def userRegistrationShouldInsertARecordIntoDB() {

    registrar.register(RegistrationRequest(email, password))

    verify(userDao).insert(User(email, null)) // this is null because we didn't stub password digester here
  }

  @Test def userRegistrationShouldDigestThePasswordBeforePersisting() {

    val digestedPassword = "4321"

    when(digester.digest(password)).thenReturn(digestedPassword)

    registrar.register(RegistrationRequest(email, password))

    verify(userDao).insert(User(email, digestedPassword))
  }

  @Test(expected = classOf[DuplicateUserEmailException])
  def userRegistrationShouldFailIfEmailIsAlreadyInUse() {

    when(userDao.insert(User(email, null))).thenThrow(new DuplicateUserEmailException(email))

    registrar.register(RegistrationRequest(email, password))
  }
}