case class HudsonBuild(number: Int, url: String)
case class HudsonHealthReport(description: String, iconUrl: String, score: Int)
class HudsonProject(val color: String, val lastBuild: HudsonBuild, val lastCompletedBuild: HudsonBuild) {

}
