///
/// This file contains the NetworkController class, which will control and manage various ActivityNetwork instances.
///
package main;

import java.util.ArrayList;

public class NetworkController {
  /// Chain of network instances. This holds the history of every project that has been open.
  ArrayList<ActivityNetwork> networkChain;

  /// Chain of timestamps, whose order corresponds with the network chain.
  ArrayList<Long> timestampChain;


}
