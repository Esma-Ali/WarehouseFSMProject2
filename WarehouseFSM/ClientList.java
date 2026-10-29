import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ClientList implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Client> clients;

    public ClientList() {
        clients = new LinkedList<Client>();
    }

    public boolean insertClient(Client client) {
        if (client == null) {
            return false;
        }

        return clients.add(client);
    }

    public Iterator<Client> getClients() {
        return clients.iterator();
    }

    public Client findClient(String clientID) {
        if (clientID == null) {
            return null;
        }

        for (Client client : clients) {
            if (client.getId().equals(clientID)) {
                return client;
            }
        }

        return null;
    }
}
