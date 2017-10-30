///
/// This file contains the NetworkStorage class, which will interact with the backend to store and retrieve networks on
/// disk.
///

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

@SuppressWarnings("unchecked")
public final class NetworkStorage {
  /**
   * Save the current network. The time at which this network was saved is returned.
   *
   * @param a Network to store.
   * @return The time in milliseconds, corresponding to the time which the network was saved.
   */
  public static long storeNetwork(ActivityNetwork a) {
    JSONArray nodeList = new JSONArray();
    JSONObject net = new JSONObject();

    for (ActivityNode n : a.getNodeList()) {
      JSONObject node = new JSONObject();
      node.put("NodeID", n.getNodeId());
      node.put("NodeName", n.getName());
      node.put("Description", n.getDescription());

      // Times are accessed as an array.
      double times[] = n.getTimes();
      node.put("NormalTime", times[0]);
      node.put("Optimistictime", times[1]);
      node.put("PessimisticTime", times[2]);

      // We store our dependency list as a comma-separated list. Store the nodes in the main list.
      node.put("DependencyNodeID", String.join(",", n.getDependencies().toString()));
      nodeList.add(node);
    }

    net.put("ProjectID", a.getNetworkId());
    net.put("NodeList", nodeList);
  }

  /**
   * Load the network (as it was last saved) from the database given the network ID.
   *
   * @param networkId ID of the network to retrieve.
   * @return An ActivityNetwork instance, corresponding to its last saved instance.
   */
  public static ActivityNetwork retrieveNetwork(long networkId) {

  }

  /**
   * Save the given network as a CSV file.
   *
   * @param a Network to export.
   */
  public static void exportNetworkAsCSV(ActivityNetwork a) {

  }

  /**
   * Import the given network (as a CSV file) into an ActivityNetwork instance.
   *
   * @param f File containing the network to import.
   */
  public static ActivityNetwork importCSVNetwork(String f) {

  }
}
