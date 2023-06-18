import 'package:shared_preferences/shared_preferences.dart';

class SharedPreferencesService {
  static SharedPreferences? _pref;

  static Future<void> initSharedPreferences() async {
    if (_pref == null) {
      _pref = await SharedPreferences.getInstance();
    }
  }

  static Future<void> setString(String prefStr, String value) async {
    // await loadPref();
    _pref?.setString(prefStr, value);
  }
}
