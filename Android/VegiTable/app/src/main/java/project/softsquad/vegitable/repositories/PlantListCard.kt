package project.softsquad.vegitable.repositories
/**
 * Data class to hold info about each plant, found on the cards in "ViewPlantList" & "ArchivePlantList" fragments
 */
data class PlantListCard (val plantImg: Int, val plantNameAndType : String, val plantPhaseOrArchiveDate: String, val plantPh: String, val plantPPM: String, val plantTemperature: String, val plantLight: String, val plantHumidity: String, val plantWater: String)