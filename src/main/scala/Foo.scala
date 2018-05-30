
final case class Job1Configuration(applicationName: String)
//final case class Job1Configuration(applicationname: String)
object Job1 extends App{
println("hello")

import com.typesafe.config.ConfigFactory
import configs.Configs
import configs.syntax._

val config = ConfigFactory.load()
    val r = config.extract[Job1Configuration]
    r.toEither match {
      case Right(s) => s
      case Left(l) =>
        throw new Exception(
          s"Failed to start. There is a problem with the configuration: ${l.messages}"
        )
    }

println(s"my config is: ${r}")
}
