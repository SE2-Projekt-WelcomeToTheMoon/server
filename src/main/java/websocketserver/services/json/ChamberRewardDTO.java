package websocketserver.services.json;

public class ChamberRewardDTO {
    final int floorIndex;
    final int chamberIndex;
    final int rocketCount;
    final int errorCount;

    public ChamberRewardDTO(int floorIndex, int chamberIndex, int rocketCount, int errorCount) {
        this.floorIndex = floorIndex;
        this.chamberIndex = chamberIndex;
        this.rocketCount = rocketCount;
        this.errorCount = errorCount;
    }
}
