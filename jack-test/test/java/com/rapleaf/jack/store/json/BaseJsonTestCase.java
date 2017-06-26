package com.rapleaf.jack.store.json;

public class BaseJsonTestCase {

  static final String ELEMENT_PATH_NAME = "test_element_path_name";
  static final String ARRAY_PATH_NAME = "test_array_path_name";
  static final int ARRAY_INDEX = 1;
  static final int ARRAY_SIZE = 5;

  public static final String COMPLEX_JSON_STRING = "{" +
      "key1: val1," +
      "key2 :" +
      "  {" +
      "    array1: [1, 2, 3]," +
      "    array2: [four, five]," +
      "    key3: val2," +
      "    deepObject:" +
      "       {" +
      "         deepKey: 0.54," +
      "         deepArray: [false, true, false]," +
      "         emptyObject: {}" +
      "       }," +
      "    deepArray: [21, 22, 23]" +
      "  }," +
      "arraysInArray :" +
      "  [" +
      "    [1, 2, 3]," +
      "    [4, 5, 6]," +
      "    [7, 8, 9]" +
      "  ]," +
      "objectsInArray :" +
      "  [" +
      "    {" +
      "      k1: [true, false, true]," +
      "      k2: 11" +
      "    }," +
      "    {" +
      "      k1: [true, false, true]," +
      "      k2: 12," +
      "      emptyArray: []" +
      "    }" +
      "  ]," +
      "array3: [6,7,8,9,10]," +
      "bool1: true," +
      "bool2: false," +
      "nullKey: null," +
      "nullStrKey: \"null\"" +
      "}";

}
