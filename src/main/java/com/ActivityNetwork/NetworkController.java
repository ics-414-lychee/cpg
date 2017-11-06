///
/// This file contains the NetworkController class, which will control and manage various ActivityNetwork instances.
///

package com.ActivityNetwork;

import java.util.ArrayList;
import java.security.SecureRandom;
import java.util.Collections;

public class NetworkController {
  /// Chain of network instances. This holds the history of every project that has been open.
  private ArrayList<ActivityNetwork> networkChain;

  /// Chain of removed network instances. Used for the "redo" method.
  private ArrayList<ActivityNetwork> removedNetworkChain;

  /// Chain of timestamps, whose order corresponds with the network chain.
  private ArrayList<Long> timestampChain;

  /// Chain of removed timestamps, whose order corresponds with the removed network chain.
  private ArrayList<Long> removedTimestampChain;

  /// The maximum length our chains can be. Will follow FIFO in event we reach this limit.
  private int maximumChainLength;

  /**
   * Constructor. We only initialize our chains here, and use a default value of 150 links for our maximum chain
   * length.
   */
  public NetworkController() {
    networkChain = new ArrayList<>();
    timestampChain = new ArrayList<>();
    removedNetworkChain = new ArrayList<>();
    removedTimestampChain = new ArrayList<>();
    maximumChainLength = 150;
  }

  /**
   * Constructor. We initialize our chains here, and set our maximum value of chain lengths. If this value is negative
   * or zero, then we default to a value of 150.
   *
   * @param maximumChainLength Maximum length of our chains.
   */
  public NetworkController(int maximumChainLength) {
    networkChain = new ArrayList<>();
    timestampChain = new ArrayList<>();
    removedNetworkChain = new ArrayList<>();
    removedTimestampChain = new ArrayList<>();
    this.maximumChainLength = (maximumChainLength < 1) ? 150 : maximumChainLength;
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
   * Append a network to our chain, along with the given timestamp. We are now unable to "redo", so clear our removed
   * chains.
   *
   * @param networkName Name to attach to the network.
   * @return The generated network ID.
   */
  public long createNetwork(String networkName) {
    SecureRandom random = new SecureRandom();
    long networkID = Math.abs(random.nextLong());

    ActivityNetwork a = new ActivityNetwork(networkID, networkName);
    appendToChains(a, System.currentTimeMillis());

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
    removedNetworkChain.clear();
    removedNetworkChain.clear();

    if (networkChain.stream().anyMatch(n -> n.getNetworkId() == a.getNetworkId())) {
      appendToChains(a, System.currentTimeMillis());
      return true;
    }
    else {
      return false;
    }
  }

  /**
   * We move the latest instance of the given network from the network class to our removed chains.
   *
   * @param networkID Network ID of the network to "remove".
   * @return True if the network was successfully "removed". False if the network does not exist.
   */
  public boolean undoNetworkChange(long networkID) {
    // We iterate through both lists backwards. Remove the first instance matching the given ID.
    for (int i = networkChain.size() - 1; i >= 0; i--) {
      if (networkChain.get(i).getNetworkId() == networkID) {
        removedNetworkChain.add(networkChain.get(i));
        removedTimestampChain.add(timestampChain.get(i));

        networkChain.remove(i);
        timestampChain.remove(i);
        return true;
      }
    }

    // There exists no network with that ID.
    return false;
  }

  /**
   * We move the latest instance of the given network from our removed chains back to the main chains.
   *
   * @param networkID Network ID of the network to re-add.
   * @return True if a change occurred. False otherwise.
   */
  public boolean redoNetworkChange(long networkID) {
    // We iterate through both lists backwards. Add the first instance matching the given ID.
    for (int i = removedNetworkChain.size() - 1; i >= 0; i--) {
      if (removedNetworkChain.get(i).getNetworkId() == networkID) {
        networkChain.add(removedNetworkChain.get(i));
        timestampChain.add(removedTimestampChain.get(i));

        removedNetworkChain.remove(i);
        removedTimestampChain.remove(i);
        return true;
      }
    }

    // There exists no network in our removed chains with that ID.
    return false;
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
        return a.clone();
      }
    }

    // Otherwise, the network does not exist. Return the list to normal.
    Collections.reverse(networkChain);
    return new ActivityNetwork(0, "");
  }

  /**
   * Access the latest instance of the given network's timestamp from the timestamp chain.
   *
   * @param networkID Network ID of the network's timestamp to retrieve.
   * @return -1 if there exists no network with the given ID. Otherwise, the timestamp corresponding to the last network
   * added.
   */
  public long retrieveTimestamp(long networkID) {
    // We iterate through the network chain backwards, and return that same spot in the timestamp chain.
    for (int i = networkChain.size() - 1; i >= 0; i--) {
      if (networkChain.get(i).getNetworkId() == networkID) {
        return timestampChain.get(i);
      }
    }

    // The network does not exist.
    return -1;
  }
}
