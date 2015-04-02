import cern.ais.gridwars.Coordinates;
import cern.ais.gridwars.UniverseView;
import cern.ais.gridwars.bot.PlayerBot;
import cern.ais.gridwars.command.MovementCommand;

import java.util.List;
import java.util.Random;

/**
 * Created by agfrg on 01/04/15.
 */
public class FABot implements PlayerBot {
    Coordinates adversaryFirst;
    Boolean firstRound = true;
    Boolean unleashHell = false;
    @Override
    public void getNextCommands(UniverseView universeView, List<MovementCommand> movementCommands) {
        if(firstRound) {
            adversaryFirst = getAdversaryFirst(universeView);
            firstRound = false;
        }

        List<Coordinates> myCoord = universeView.getMyCells();

        for(Coordinates coord : myCoord) {
            if(universeView.getPopulation(coord) >= 2 && unleashHell) {
                Long movement = universeView.getPopulation(coord) / 2;
                movementCommands.add(new MovementCommand(coord, getRandomDirection(), movement));
            } else if (universeView.getPopulation(coord) >= 2) {
                DirectionReturn dir = getClosestDirection(coord);
                if(dir.dist < 5) {
                    unleashHell = true;
                }
                movementCommands.add(new MovementCommand(coord, dir.dir, universeView.getPopulation(coord)));
            }
        }
    }

    private MovementCommand.Direction getRandomDirection() {
        Random rand = new Random();
        switch (rand.nextInt() % 4) {
            case 0: return MovementCommand.Direction.DOWN;
            case 1: return MovementCommand.Direction.LEFT;
            case 2: return MovementCommand.Direction.UP;
            case 3: return MovementCommand.Direction.RIGHT;
            default: return MovementCommand.Direction.RIGHT;

        }
    }

    private DirectionReturn getClosestDirection(Coordinates coord) {
        Double distance = getDistance(coord, adversaryFirst);
        DirectionReturn ret = null;

        Double leftDistance = getDistance(coord.getLeft(), adversaryFirst);
        if (leftDistance < distance) {
            distance = leftDistance;
            ret = new DirectionReturn();
            ret.dir = MovementCommand.Direction.LEFT;
            ret.dist = distance;
        }

        Double rightDistance = getDistance(coord.getRight(), adversaryFirst);
        if (rightDistance < distance) {
            distance = rightDistance;
            ret = new DirectionReturn();
            ret.dir = MovementCommand.Direction.RIGHT;
            ret.dist = distance;
        }

        Double upDistance = getDistance(coord.getUp(), adversaryFirst);
        if (upDistance < distance) {
            distance = upDistance;
            ret = new DirectionReturn();
            ret.dir = MovementCommand.Direction.UP;
            ret.dist = distance;
        }

        Double downDistance = getDistance(coord.getDown(), adversaryFirst);
        if (downDistance < distance) {
            distance = downDistance;
            ret = new DirectionReturn();
            ret.dir = MovementCommand.Direction.DOWN;
            ret.dist = distance;
        }

        if(ret == null) {
            throw new RuntimeException();
        } else {
            return ret;
        }
    }

    private Double getDistance(Coordinates x, Coordinates y) {
        Integer xCoord = Math.abs(x.getX() - y.getX());
        Integer yCoord = Math.abs(x.getY() - y.getY());
        return Math.sqrt(Math.pow(xCoord, 2) + Math.pow(yCoord, 2));
    }

    private Coordinates getAdversaryFirst(UniverseView universeView) {
        int universeSide = universeView.getUniverseSize();

        for (int i = 0; i < universeSide; i++) {
            for (int j = 0; j < universeSide; j++) {
                Coordinates coord = universeView.getCoordinates(i, j);
                if(!(universeView.isEmpty(coord) || universeView.belongsToMe(coord))) {
                    return coord;
                }
            }
        }

        throw new RuntimeException();
    }
}
