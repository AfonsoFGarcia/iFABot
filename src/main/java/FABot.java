import cern.ais.gridwars.Coordinates;
import cern.ais.gridwars.UniverseView;
import cern.ais.gridwars.bot.PlayerBot;
import cern.ais.gridwars.command.MovementCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agfrg on 01/04/15.
 */
public class FABot implements PlayerBot {
    private Integer side;
    private Boolean firstTurn = true;

    @Override
    public void getNextCommands(UniverseView universeView, List<MovementCommand> movementCommands) {
        if(firstTurn) {
            side = universeView.getUniverseSize();
            firstTurn = false;
        }

        List<Coordinates> myCoord = universeView.getMyCells();

        for(Coordinates coord : myCoord) {
            if(universeView.getPopulation(coord) >= 2) {
                Long movement = universeView.getPopulation(coord) / 2;
                MovementCommand.Direction direction = getDirection(universeView, coord);
                if(direction != null)
                    movementCommands.add(new MovementCommand(coord, direction, movement));
            }
        }
    }

    private MovementCommand.Direction getDirection(UniverseView universeView, Coordinates myCoord) {
        Coordinates theirCoord = getClosest(universeView, myCoord);

        Double distance = getDistance(myCoord, theirCoord);
        MovementCommand.Direction direction = null;

        if(getDistance(myCoord.getDown(), theirCoord) < distance) {
            distance = getDistance(myCoord.getDown(), theirCoord);
            direction = MovementCommand.Direction.DOWN;
        }

        if(getDistance(myCoord.getUp(), theirCoord) < distance) {
            distance = getDistance(myCoord.getUp(), theirCoord);
            direction = MovementCommand.Direction.UP;
        }

        if(getDistance(myCoord.getLeft(), theirCoord) < distance) {
            distance = getDistance(myCoord.getLeft(), theirCoord);
            direction = MovementCommand.Direction.LEFT;
        }

        if(getDistance(myCoord.getRight(), theirCoord) < distance) {
            direction = MovementCommand.Direction.RIGHT;
        }

        return direction;
    }

    private List<Coordinates> getAdversaryCoordinates(UniverseView universeView) {
        ArrayList<Coordinates> adversary = new ArrayList<Coordinates>();

        for(int i = 0; i < side; i++) {
            for(int j = 0; j < side; j++) {
                Coordinates coord = universeView.getCoordinates(i, j);
                if(!(universeView.isEmpty(coord) || universeView.belongsToMe(coord))) {
                    adversary.add(coord);
                }
            }
        }

        return adversary;
    }

    private Double getDistance(Coordinates myCoord, Coordinates theirCoord) {
        return Math.sqrt(Math.pow(myCoord.getX()-theirCoord.getX(), 2) + Math.pow(myCoord.getY() - theirCoord.getY(), 2));
    }

    private Coordinates getClosest(UniverseView universeView, Coordinates myCoord) {
        Double distance = Double.MAX_VALUE;
        Coordinates closest = null;

        for(Coordinates theirCoord : getAdversaryCoordinates(universeView)) {
            Double dist = getDistance(myCoord, theirCoord);
            if(dist < distance) {
                distance = dist;
                closest = theirCoord;
            }
        }

        return closest;
    }
}
