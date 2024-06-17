package websocketserver.game.util;

import websocketserver.game.enums.FieldValue;
import websocketserver.game.model.CardCombination;

/**
 * This is a Skeleton for easy receiving Messages from Client in a proper Format
 */
public record FieldUpdateMessage(int floor, int chamber, int field, FieldValue fieldValue, String userOwner, CardCombination cardCombination){
}
