package com.example.map_proiect_extins.service;

import com.example.map_proiect_extins.domain.Friendship;
import com.example.map_proiect_extins.domain.Graph;
import com.example.map_proiect_extins.domain.Pair;
import com.example.map_proiect_extins.domain.User;
import com.example.map_proiect_extins.repository.Repository;

import java.util.ArrayList;

//userRepo.findAll-nodurile getFriends(lista prieteni useri)-muchiile
public class ConnectionService {
    private Repository<Long, User> userRepository;
    private Repository<Pair<Long>, Friendship> friendshipRepository;
    private ArrayList<ArrayList<User>> components = new ArrayList<>();

    public ConnectionService(Repository<Long, User> userRepository, Repository<Pair<Long>, Friendship> friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }

    /**
     * Looking for maximum id
     *
     * @return maximum id
     */
    private Long maxID() {
        Long idMax = 0L;
        for (User u : userRepository.findAll()) {
            if (u.getId() > idMax)
                idMax = u.getId();
        }
        return idMax;
    }

    /**
     * Creating a graph
     *
     * @return a graph for communities
     */
    private Graph createGraph() {
        Long idMax = maxID();
        Graph graph = new Graph(idMax.intValue() + 1);

        for (Friendship friendship : friendshipRepository.findAll()) {
            graph.addEdge(friendship.getId().getFirst().intValue(), friendship.getId().getSecond().intValue());
        }

        return graph;
    }

    public ArrayList<ArrayList<Integer>> connectedComponents() {
        Graph graph = createGraph();
        return graph.connectedComponents();
    }

    public ArrayList<Integer> theMostSociableCommunity() {
        Graph graph = createGraph();
        return graph.longestComp();
    }

}
