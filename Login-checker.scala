package login

import errorCodeHandler.LoginErrorCodeHandler
import user.User

import scala.collection.mutable.LinkedHashMap

class LoginChecker(userID: String, password: String):

  def checkFields(userID: String, password: String): String =
    val userCheck = LoginErrorCodeHandler.errorCodeHandler(checkUserID(userID))
    val passwordCheck = LoginErrorCodeHandler.errorCodeHandler(checkPassword(password))
    val checkResponseList = List(userCheck, passwordCheck).filter(str => str != "OK")
    
    checkResponseList match
      case response if response.isEmpty => "0K"
      case _ => checkResponseList.head


  private def checkUserID(userID: String): String =
    userID match
      case id if id.isBlank => "LOGIN_USERID_1"
      case _ => "OK"

  private def checkPassword(password: String): String =
    password match
      case userPassword if userPassword.isBlank => "LOGIN_PASSWORD_1"
      case _ => "OK"


