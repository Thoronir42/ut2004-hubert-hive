package cz.zcu.students.kiwi.ctf.mapView;

import cz.cuni.amis.pathfinding.map.IPFMapView;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;

import java.util.Collection;

public class StandardMapView implements IPFMapView<NavPoint> {
    @Override
    public Collection<NavPoint> getExtraNeighbors(NavPoint node, Collection<NavPoint> mapNeighbors) {
        return null;
    }

    @Override
    public int getNodeExtraCost(NavPoint node, int mapCost) {
        return 0;
    }

    @Override
    public int getArcExtraCost(NavPoint nodeFrom, NavPoint nodeTo, int mapCost) {
        return 0;
    }

    @Override
    public boolean isNodeOpened(NavPoint node) {
        return true;
    }

    @Override
    public boolean isArcOpened(NavPoint nodeFrom, NavPoint nodeTo) {
        return true;
    }
}
