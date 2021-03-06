package com.ActivityNetwork;

import com.BaseInterface.UserAccount;

import java.util.ArrayList;
import java.util.Collections;

/**
 * The NetworkController class, which controls and manages various ActivityNetwork instances.
 */
public class NetworkController {
  /** Chain of network instances. This holds the history of every project that has been open. */
  private ArrayList<ActivityNetwork> networkChain;

  /** Chain of removed network instances. Used for the "redo" method. */
  private ArrayList<ActivityNetwork> removedNetworkChain;

  /** Chain of timestamps, whose order corresponds with the network chain. */
  private ArrayList<Long> timestampChain;

  /** Chain of removed timestamps, whose order corresponds with the removed network chain. */
  private ArrayList<Long> removedTimestampChain;

  /** The maximum length our chains can be. Will follow FIFO in event we reach this limit. */
  private int maximumChainLength;

  /** The username associated with this controller. Obtained from a successful login. */
  private String u;

  /** The authentication token associated with this controller. Obtained from a successful login. */
  private String token;

  /** JSON set of project names, IDs and deadlines who are related by their indices. Obtained from successful login. */
  private String j;

  /**
   * Constructor. We initialize our chains here, and use the given value for our maximum chain length. If this value
   * is negative or zero, then we default to a value of 150.
   *
   * @param u                  Username associated with the controller. Obtained from a successful login.
   * @param token              Authentication token associated with this controller. Obtained from a successful login.
   * @param j                  ProjectsJSON returned from a successful login.
   * @param maximumChainLength Maximum length of our chains.
   */
  @SuppressWarnings("WeakerAccess")
  public NetworkController(String u, String token, String j, int maximumChainLength) {
    this.u = u;
    this.token = token;
    this.j = j;
    this.maximumChainLength = (maximumChainLength < 1) ? 150 : maximumChainLength;

    this.networkChain = new ArrayList<>();
    this.timestampChain = new ArrayList<>();
    this.removedNetworkChain = new ArrayList<>();
    this.removedTimestampChain = new ArrayList<>();

    // Load our networks and network names into our chains.
    UserAccount.idsFromProjectJSON(j).forEach(this::loadNetwork);
  }

  /**
   * Constructor. We initialize our chains here, and use a default value of 150 links for our maximum chain
   * length.
   *
   * @param u     Username associated with the controller. Obtained from a successful login.
   * @param token Authentication token associated with this controller. Obtained from a successful login.
   * @param j     ProjectsJSON returned from a successful login.
   */
  public NetworkController(String u, String token, String j) {
    this(u, token, j, 150);
  }

  /**
   * Appends the given element to the end of our chains. If we have reached our maximum chain length, remove the first
   * element and append the elements as normal. We are now unable to "redo", so clear our removed chains.
   *
   * @param a ActivityNetwork instance to add to our network chain.
   * @param t Timestamp to add to our timestamp chain.
   */
  private void appendToChains(ActivityNetwork a, long t) {
    if (networkChain.size() > maximumChainLength) {
      networkChain.remove(0);
      timestampChain.remove(0);
    }

    networkChain.add(a);
    timestampChain.add(t);

    removedNetworkChain.clear();
    removedTimestampChain.clear();
  }

  /**
   * Load the network with the given ID into our network chain.
   *
   * @param networkID Network to load into our chain.
   * @return True if the network with the given ID exists and was loaded correctly. False otherwise.
   */
  private boolean loadNetwork(long networkID) {
    ActivityNetwork a = NetworkStorage.retrieveNetwork(token, u, j, networkID);

    if (a.getNetworkId() == 0) {
      return false;

    } else {
      // Network exists. We attach our network to our chains with the current timestamp.
      appendToChains(a, System.currentTimeMillis());
      return true;
    }
  }

  /**
   * Append a network to our chain and project JSON, along with the given timestamp. We are now unable to "redo", so
   * clear our removed chains.
   *
   * @param networkName Name to attach to the network.
   * @return 0 if there is an error. Otherwise, the generated network ID.
   */
  public long createNetwork(String networkName) {
    long networkID = NetworkStorage.createNetwork(token, u, networkName);
    if (networkID == 0) {
      return 0;
    }

    ActivityNetwork a = new ActivityNetwork(networkID, networkName);
    appendToChains(a, System.currentTimeMillis());
    j = UserAccount.insertIntoProjectJSON(j, a);

    removedNetworkChain.clear();
    removedTimestampChain.clear();
    return networkID;
  }

  /**
   * Append the given network to our chain, but with a new timestamp. Only works if we find a network here with
   * given network ID. The intent here is not add networks that were generated outside of this class. We are now unable
   * to "redo", so clear our removed chains.
   *
   * @param a ActivityNetwork instance to add.
   * @return True if the modification was successful. False if there exists no network here with the given network ID.
   */
  public boolean modifyNetwork(ActivityNetwork a) {
    if (networkChain.stream().noneMatch(n -> n.getNetworkId() == a.getNetworkId())) {
      return false;
    }

    removedNetworkChain.clear();
    removedNetworkChain.clear();

    appendToChains(a, System.currentTimeMillis());
    return true;
  }

  /**
   * Move the network with the given ID from our network chain to our removed chain if the flag is up. Otherwise, we
   * perform the inverse action and move the network with the given ID from our removed chain to our network chain.
   *
   * @param networkID    Network ID of the network to move.
   * @param isUndoAction If true, perform the undo action. Otherwise, perform the redo action.
   * @return True if a network was successfully transferred. False if the network does not exist.
   */
  private boolean moveBetweenChains(long networkID, boolean isUndoAction) {
    ArrayList<ActivityNetwork> sourceChain, targetChain;
    ArrayList<Long> sourceTimestampChain, targetTimestampChain;

    // Look... There's a weird bug that's too far in for me to find. This is as dirty as it gets.
    int s = (isUndoAction) ? 1 : 0;

    // Determine which is the source, and which is the target.
    if (isUndoAction) {
      sourceChain = networkChain;
      targetChain = removedNetworkChain;
      sourceTimestampChain = timestampChain;
      targetTimestampChain = removedTimestampChain;
    } else {
      sourceChain = removedNetworkChain;
      targetChain = networkChain;
      sourceTimestampChain = removedTimestampChain;
      targetTimestampChain = timestampChain;
    }

    // We iterate through both lists backwards. Remove the first instance matching the given ID.
    for (int i = sourceChain.size() - 1; i >= s; i--) {
      if (sourceChain.get(i).getNetworkId() == networkID) {
        targetChain.add(sourceChain.get(i));
        targetTimestampChain.add(sourceTimestampChain.get(i));

        sourceChain.remove(i);
        sourceTimestampChain.remove(i);
        return true;
      }
    }

    // There exists no network with that ID.
    return false;
  }

  /**
   * We move the latest instance of the given network from the network class to our removed chains.
   *
   * @param networkID Network ID of the network to "remove".
   * @return True if the network was successfully "removed". False if the network does not exist.
   */
  public boolean undoNetworkChange(long networkID) {
    return moveBetweenChains(networkID, true);
  }

  /**
   * We move the latest instance of the given network from our removed chains back to the main chains.
   *
   * @param networkID Network ID of the network to re-add.
   * @return True if a change occurred. False otherwise.
   */
  public boolean redoNetworkChange(long networkID) {
    return moveBetweenChains(networkID, false);
  }

  /**
   * Access the latest instance of the given network from the network chain.
   *
   * @param networkID Network ID of the network to retrieve.
   * @return An empty network if there exists no network with that ID. Otherwise, the latest instance of that network
   * matching the given ID.
   */
  public ActivityNetwork retrieveNetwork(long networkID) {
    Collections.reverse(networkChain);
    for (ActivityNetwork a : networkChain) {

      // If we find the network, return the list to normal and return **a clone** of the network we found.
      if (a.getNetworkId() == networkID) {
        Collections.reverse(networkChain);
        return a.twin();
      }
    }

    // Otherwise, the network does not exist. Return the list to normal.
    Collections.reverse(networkChain);
    return new ActivityNetwork(0, "Bad");
  }

  /**
   * Save the latest instance of the given network in our database.
   *
   * @param networkID ID of the network to save.
   * @return True if the network was correctly saved. False otherwise.
   */
  public boolean storeNetwork(long networkID) {
    Collections.reverse(networkChain);
    for (ActivityNetwork a : networkChain) {

      // If we find the network, save this.
      if (a.getNetworkId() == networkID) {
        Collections.reverse(networkChain);

        // Update our Project JSON if successful.
        String response = NetworkStorage.storeNetwork(token, u, j, a);
        if (!response.equals("")) {
          j = response;
          return true;

        } else {
          return false;
        }
      }
    }

    // Otherwise, the network does not exist. No saving can be performed.
    Collections.reverse(networkChain);
    return false;
  }

  /**
   * Remove all instances of that network from our chains, and remove it from database as well. This action is
   * irreversible! We are now unable to "redo", so clear our removed chains.
   *
   * @param networkID ID of the network to remove.
   * @return True  if the network was deleted from the backend and our chains. False if the network does not exist.
   */
  public boolean deleteNetwork(long networkID) {
    boolean removedFromChains = false;

    for (int i = 0; i < networkChain.size(); i++) {
      if (networkChain.get(i).getNetworkId() == networkID) {
        networkChain.remove(i);
        timestampChain.remove(i);
        removedFromChains = true;
      }
    }

    removedNetworkChain.clear();
    removedTimestampChain.clear();

    // We must remove from the chains, and delete the network from the database.
    String response = NetworkStorage.deleteNetwork(token, u, j, networkID);
    if (!response.equals("")) {
      j = response;
    }

    return removedFromChains && !response.equals("");
  }

  /**
   * Access the latest instance of the given network's timestamp from the timestamp chain.
   *
   * @param networkID Network ID of the network's timestamp to retrieve.
   * @return -1 if there exists no network with the given ID. Otherwise, the timestamp corresponding to the last network
   * added.
   */
  long retrieveTimestamp(long networkID) {
    // We iterate through the network chain backwards, and return that same spot in the timestamp chain.
    for (int i = networkChain.size() - 1; i >= 0; i--) {
      if (networkChain.get(i).getNetworkId() == networkID) {
        return timestampChain.get(i);
      }
    }

    // The network does not exist.
    return -1;
  }

  /**
   * Accessor method for the most current project JSON.
   *
   * @return The most current project JSON.
   */
  public String getProjectJSON() {
    return j;
  }
}
