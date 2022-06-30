package usageGenerator

import io.circe.syntax
import io.circe.syntax.*
import mongoDriver.MongoDB
import org.bson.json.JsonReader
import org.mongodb.scala.{FindObservable, MongoClient, MongoCollection, Observer}
import org.mongodb.scala.bson.BsonString
import org.mongodb.scala.bson.collection.immutable.Document
import user.User
import mongoDriver.Helpers.*
import mongoDriver.MongoDB.retrieveUsers

import scala.collection.mutable.*
import scala.language.postfixOps
import scala.util.Random

object UsageGenerator:

  def generation(): Unit =
    var month = getActualMonthOrYear("month")
    var year = getActualMonthOrYear("year")
    val usagesCollection = MongoDB.mongoDBConnection().getCollection("usages")

    while true do
      val userListFromDatabase: ListBuffer[User] = retrieveUsers()
      val usageTypes: List[String] = List("water", "heat", "electricity")
      for user <- userListFromDatabase do
        for usageType <- usageTypes do
          val userUsage = composeUsageMap(user,usageType, month, year)
          usagesCollection.insertOne(Document(userUsage)).results()

      month += 1
      if (month == 13) then
        month = 1
        year += 1

      Thread.sleep(10000)

  private def getActualMonthOrYear(monthOrYear: String): Int =
    var monthOrYearCounter = 0
    val usagesCollection = MongoDB.mongoDBConnection().getCollection("usages")

    monthOrYear match
      case month if month.toString.equalsIgnoreCase("month") =>
        monthOrYearCounter = 1
        usagesCollection.countDocuments().results().last match
          case 0 => monthOrYearCounter = 1
          case _ => monthOrYearCounter = usagesCollection.find().results().last("year").asString().getValue.toInt

      case year if year.toString.equalsIgnoreCase("year") =>
        monthOrYearCounter = 1970
        usagesCollection.countDocuments().results().last match
          case 0 => monthOrYearCounter = 1970
          case _ => monthOrYearCounter = usagesCollection.find().results().last("year").asString().getValue.toInt

    monthOrYearCounter

  private def composeUsageMap(user: User, usageType: String, month: Int, year: Int): LinkedHashMap[String, BsonString] =

    val userUsage: LinkedHashMap[String, BsonString] = LinkedHashMap()
    userUsage("userID") = BsonString.apply(user.getUserID)
    userUsage("userType") = BsonString.apply(user.getUserType)
    userUsage("city") = BsonString.apply(user.getCity)
    userUsage("region") = BsonString.apply(user.getRegion)
    userUsage("usageType") = BsonString.apply(usageType)
    userUsage("usage") = BsonString.apply((Random.nextDouble()/100).toString)
    userUsage("cost") = BsonString.apply((Random.nextDouble()/100).toString)
    userUsage("month") = BsonString.apply(month.toString)
    userUsage("year") = BsonString.apply(year.toString)

    userUsage
