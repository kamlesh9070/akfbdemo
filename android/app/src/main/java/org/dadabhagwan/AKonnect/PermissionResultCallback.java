package org.dadabhagwan.AKonnect;

import java.util.ArrayList;

/**
 * Created by Kamlesh on 02-12-2017.
 */

interface PermissionResultCallback {
  void PermissionGranted(int request_code);

  void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions);

  void PermissionDenied(int request_code);

  void NeverAskAgain(int request_code);
}
