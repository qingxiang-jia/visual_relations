/**
 * POJO that simply translates numerical id of a building into String.
 * id is the value of the building - 1.
 */
public class Id2Name
{
    final static String[] buildingNames = new String[] {
            null,
            "Pupin",
            "Schapiro CEPSR",
            "Mudd, Engineering Terrace, Fairchild & Computer Science",
            "Physical Fitness Center",
            "Gymnasium & Uris",
            "Schermerhorn",
            "Chandler & Havemeyer",
            "Computer Center",
            "Avery",
            "Fayerweather",
            "Mathematics",
            "Low Library",
            "St. Paul's Chapel",
            "Earl Hall",
            "Lewisohn",
            "Philosophy",
            "Buell & Maison Francaise",
            "Alma Mater",
            "Dodge",
            "Kent",
            "College Walk",
            "Journalism & Furnald",
            "Hamilton, Hartley, Wallach & John Jay",
            "Lion's Court",
            "Lerner Hall",
            "Butler Library",
            "Carman"
    };

    public static String getName(int id)
    {
        return buildingNames[id];
    }
}
