import java.io.FileReader;
import java.util.*;
import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.BasicDBObject;
import org.bson.Document;
import java.io.File;
import static java.util.Arrays.asList;
import java.util.logging.Logger;
import java.util.logging.Level;


/* To compile and run from Windows (testad on 'cmd' and 'sigwin'):
 * javac -cp "gson-2.8.2.jar;json-simple-1.1.jar;mongo-java-driver-3.9.1.jar;." Main.java && java -cp "gson-2.8.2.jar;json-simple-1.1.jar;mongo-java-driver-3.9.1.jar;." Main
 *
 * To compile and run from Linux (not testad):
 * javac -cp gson-2.8.2.jar:json-simple-1.1.jar:mongo-java-driver-3.9.1.jar:. Main.java && java -cp gson-2.8.2.jar:json-simple-1.1.jar:mongo-java-driver-3.9.1.jar:. Main
 */


/**
 * Main class start application
 * @author Maksym
 * @since 2019-01-31
 */
public class Main {

    /**
     * Create mongodb client
     */
    static MongoClient mongoClient = new MongoClient();

    /**
     * Connect to test database
     */
    static MongoDatabase database;

    /**
     *  Make the logger a little more quiet, get database 'lab3'
     */
    static {
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);
        System.out.println("\nACTIVE DATABASE: 'lab3'...\n");
        database = mongoClient.getDatabase("lab3");
    }

    /**
     * List for importing data from data/restaurants.json
     */
    static List<Restaurant> restaurants = new ArrayList<>();

    /**
     *  Lists all databases and print out
     */
    static void showAllDatabases() {
        System.out.println("===================== Databases =====================");
        for(String database : mongoClient.listDatabaseNames()){
            System.out.println("  * "+database);
        }
        System.out.println();
    }

    /**
     *  Lists all collections and print out
     */
    static void showAllCollections() {
        System.out.println("===================== Collections =====================");
        boolean collectionExist = false;
        for (String collectionName : database.listCollectionNames()) {
            System.out.println("  * "+collectionName);
            collectionExist = true;
        }
        if(!collectionExist){
            System.out.println("  empty...");
        }
        System.out.println();
    }

    /**
     * Create collection
     * @param collectionName is collection name
     */
    static void createCollection(String collectionName) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
//        collection.insertOne(new Document());
        System.out.println("<<< Collection '"+collectionName+"' was successfully created >>>" );
    }

    /**
     * Delete collection
     * @param collectionName is collection name
     */
    static void deleteCollection(String collectionName) {
        boolean collectionExist = false;
        for (String colName : database.listCollectionNames()) {
            if(colName.equals(collectionName)) {
                collectionExist = true;
            }
        }
        if(collectionExist){
            MongoCollection<Document> collection = database.getCollection(collectionName);
            collection.drop();
            collection = database.getCollection(collectionName);
            System.out.println("<<< Collection '"+collectionName+"' was successfully deleted >>>\n" );
        } else {
            System.out.println("<<< Collection '"+collectionName+"' was not found >>>\n" );
        }
    }

    /**
     * Import data from json file and set it to list 'restaurants',
     * this method run createCollection() and populateRestaurants()
     * @param fileName is json file name in 'data' folder, without extensions, ex 'restaurants'
     */
    static void loadDataFromJsonFile(String fileName){
        String path = System.getProperty("user.dir") + File.separator + "data" + File.separator;
        File[] files = new File(path).listFiles();
        boolean jsonFileExistInPath = false;
        for (File f : files) {
            if (f.isFile()) {
                if(f.getName().equals(fileName+".json")){
                    jsonFileExistInPath = true;
                }
            }
        }
        if (jsonFileExistInPath){
            try{
                createCollection(fileName);
                FileReader fr = new FileReader(path + fileName + ".json");
                Restaurant[] reviews = new Gson().fromJson(fr, Restaurant[].class);
                restaurants = Arrays.asList(reviews);
                populateRestaurants();
                System.out.println("<<< All data from '"+fileName+".json' was successfully imported to collection 'restaurants' >>>\n" );
            }catch (Exception e){
                System.out.println("<<< Error on '"+fileName+".json' import >>>\n" );
                e.printStackTrace();
            }
        } else{
            System.out.println("<<< '"+fileName+".json' was not found in the path (\""+path+"\") >>>\n" );
        }
    }

    /**
     * Populate collection 'restaurants' with all data from list 'restaurants'
     */
    static void populateRestaurants() {
        try{
            MongoCollection<Document> collection = database.getCollection("restaurants");
            for(Restaurant restaurant : restaurants){
                List<Document> document = asList(
                        new Document("_id", restaurant.get_id())
                                .append("name", restaurant.getName())
                                .append("stars", restaurant.getStars())
                                .append("categories", restaurant.getCategories()));
                collection.insertMany(document);
            }
        }catch (Exception e){
        }
    }

    /**
     * Lists and print out all documents in current collection
     * @param collectionName is collection name
     */
    static void printCollection(String collectionName) {
        System.out.println("All documents:");
        MongoCollection<Document> collection = database.getCollection(collectionName);
        List<Document> result = collection.find().into(new ArrayList<>());
        result.stream().map(Document::toJson).forEach(System.out::println);
        System.out.println();
    }

    /**
     * Search in collection by field and value
     * for example search alla restaurants with 'Cafe' in field 'categories'
     * @param searchCollection is collection name for searching
     * @param searchField is field for searching
     * @param searchValue is a text to find
     */
    static void search(String searchCollection, String searchField, String searchValue) {
        MongoCollection<Document> collection = database.getCollection(searchCollection);
        List<Document> result = collection.find(new Document(searchField, searchValue)).into(new ArrayList<>());
        System.out.println("All "+searchValue+":");
        if (result.isEmpty()){
            System.out.println("<<< Field '"+searchField+"' with value '"+searchValue+"' not found >>>" );
        } else{
            for(Document item : result){
                System.out.println("  * " + item.get("name"));;
            }
        }
        System.out.println();
    }

    /**
     * Update field value by incrementing
     * Increment 'stars' in restaurant with name 'XYZ Coffe Bar' by 1
     */
    static void incrementStars() {
        MongoCollection<Document> collection = database.getCollection("restaurants");
        BasicDBObject updateQuery = new BasicDBObject();
        updateQuery.append("$inc", new BasicDBObject().append("stars", 1));
        BasicDBObject searchQuery = new BasicDBObject().append("name", "XYZ Coffee Bar");
        collection.updateOne(searchQuery, updateQuery);
        System.out.println("<<< Stars for 'XYZ Coffe Bar' was incremented successfully >>>\n" );
    }

    /**
     * Update field value, common method
     * for example update "name" from "456 Cookies Shop" to "123 Cookies Heaven"
     * @param field is a field for udate
     * @param originalName is current field value, value you want to change
     * @param newName is new field value, value you want to set
     */
    static void updateField(String field, String originalName, String newName) {
        MongoCollection<Document> collection = database.getCollection("restaurants");
        BasicDBObject updateQuery = new BasicDBObject();
        updateQuery.append("$set", new BasicDBObject().append("name", newName));
        BasicDBObject searchQuery = new BasicDBObject().append("name", originalName);
        collection.updateOne(searchQuery, updateQuery);
        System.out.println("<<< Restaurant '"+originalName+"' was renamed to '"+newName+"' successfully >>>\n" );
    }

    /**
     * Helping method for createn stars symboler for restaurants
     * for example update "*" 4 times => "****"
     * @param c char symbol for repeating
     * @param times is a times to repeat symbol
     * @return String n times
     */
    static String repeat(char c, int times){
        StringBuffer b = new StringBuffer();
        for(int i=0; i < times; i++){
            b.append(c);
        }
        return b.toString();
    }

    /**
     * Make aggregation for mongodb,
     * group all restaurants with stars 4 or more and print out names and stars
     */
    static void aggregation() {
        System.out.println("Aggregation (4 or more stars):");
        MongoCollection<Document> collection = database.getCollection("restaurants");
        AggregateIterable<Document> output = collection.aggregate(Arrays.asList(
                new Document("$match", new Document("stars", new Document("$gte", 4))),
                new Document("$sort", new Document("name", 1))
        ));
        boolean resultIsEmpty = true;
        for (Document dbObject : output) {
            System.out.println("  * "+dbObject.get("name")+": "+dbObject.get("stars") + "("+ repeat('*', (int)dbObject.get("stars")) +")");
            resultIsEmpty = false;
        }
        if(resultIsEmpty){
            System.out.println("<<< Aggregation error, no result data >>>\n" );
        }
        System.out.println();
    }

    /* Main method, start application
     *
     */
    public static void main(String[] args) {
        String collectionName = "restaurants";
        showAllDatabases();
        showAllCollections();

        loadDataFromJsonFile(collectionName);
        showAllCollections();
        printCollection(collectionName);

        deleteCollection(collectionName);
        showAllCollections();

        loadDataFromJsonFile(collectionName);
        showAllCollections();
        printCollection(collectionName);

        search(collectionName, "categories", "Cafe");
        incrementStars();
        printCollection(collectionName);

        updateField("name", "456 Cookies Shop" , "123 Cookies Heaven");
        printCollection(collectionName);

        aggregation();

        deleteCollection(collectionName);
    }
}
/* To compile and run from Windows (tested on 'cmd' and 'sigwin'):
 * javac -cp "gson-2.8.2.jar;json-simple-1.1.jar;mongo-java-driver-3.9.1.jar;." Main.java && java -cp "gson-2.8.2.jar;json-simple-1.1.jar;mongo-java-driver-3.9.1.jar;." Main
 *
 * To compile and run from Linux (not tested):
 * javac -cp gson-2.8.2.jar:json-simple-1.1.jar:mongo-java-driver-3.9.1.jar:. Main.java && java -cp gson-2.8.2.jar:json-simple-1.1.jar:mongo-java-driver-3.9.1.jar:. Main
 */
