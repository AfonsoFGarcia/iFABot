import cern.ais.gridwars.Coordinates;
import cern.ais.gridwars.UniverseView;
import cern.ais.gridwars.bot.PlayerBot;
import cern.ais.gridwars.command.MovementCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by agfrg on 01/04/15.
 */
public class FABot implements PlayerBot {
    private Integer side;
    private Boolean firstTurn = true;
    Random rand = new Random();

    @Override
    public void getNextCommands(UniverseView universeView, List<MovementCommand> movementCommands) {
        List<Coordinates> myCoord = universeView.getMyCells();
        if(firstTurn) {
            side = universeView.getUniverseSize();
            firstTurn = false;

            for(Coordinates coord : myCoord) {
                Long movement = universeView.getPopulation(coord)/4;
                movementCommands.add(new MovementCommand(coord, MovementCommand.Direction.LEFT, movement));
                movementCommands.add(new MovementCommand(coord, MovementCommand.Direction.RIGHT, movement));
                movementCommands.add(new MovementCommand(coord, MovementCommand.Direction.UP, movement));
                movementCommands.add(new MovementCommand(coord, MovementCommand.Direction.DOWN, movement));
            }
        } else {
            for (Coordinates coord : myCoord) {
                Long movement = universeView.getPopulation(coord) - (long) (universeView.getPopulation(coord)/5);
                MovementCommand.Direction direction = null;
                direction = getDirection(universeView, coord, direction);
                if (direction != null && canMove(universeView, coord, direction, movement))
                    movementCommands.add(new MovementCommand(coord, direction, movement));
            }
        }
    }

    public int randInt(int min, int max) {
        return rand.nextInt((max - min) + 1) + min;
    }

    private MovementCommand.Direction getDirection(UniverseView universeView, Coordinates coord, MovementCommand.Direction direction) {
        Boolean doRandom = randInt(1, 100) > 90;
        if(!doRandom && universeView.getPopulation(coord) >= universeView.getMaximumPopulation()/4) {
            direction = getDirection(universeView, coord);
        } else if (universeView.getPopulation(coord) >= 2) {
            direction = getRandomDirection();
        }
        return direction;
    }

    private Boolean canMove(UniverseView universeView, Coordinates myCoord, MovementCommand.Direction dir, Long numPeople) {
        Coordinates next = null;
        switch (dir) {
            case DOWN: next = myCoord.getDown(); break;
            case UP: next = myCoord.getUp(); break;
            case LEFT: next = myCoord.getLeft(); break;
            case RIGHT: next = myCoord.getRight(); break;
        }
        Long newPeople = numPeople + universeView.getPopulation(next);
        return (!universeView.belongsToMe(next) || newPeople <= universeView.getMaximumPopulation()) && numPeople > 0;
    }

    private MovementCommand.Direction getRandomDirection() {
        switch (rand.nextInt() % 4) {
            case 0: return MovementCommand.Direction.DOWN;
            case 1: return MovementCommand.Direction.LEFT;
            case 2: return MovementCommand.Direction.UP;
            case 3: return MovementCommand.Direction.RIGHT;
            default: return MovementCommand.Direction.RIGHT;

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
