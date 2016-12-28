package util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import service.DatabaseUtility;

public class ProductUtility extends DatabaseUtility{
    private static final String PRODUCT_COLLECTION = "Product";
    private static final String NAME = "name";
    private static ProductUtility INSTANCE = new ProductUtility();
    private MongoCollection<Document> collection;
    
    private ProductUtility() {
        collection = db.getCollection(PRODUCT_COLLECTION);
    }
    public static ProductUtility getInstance() {
        return INSTANCE;
    }
    public void insert(String product){
        Document document = new Document(NAME,product);
        collection.insertOne(document);
    }
    public Set<String> getAll(){
        Set<String> products = new HashSet<>();
        FindIterable<Document> documents = collection.find();
        documents.forEach(new Consumer<Document>() {
            @Override
            public void accept(Document doc) {
                products.add((String) doc.get(NAME));
            }
        });
        return products;
    }
    
    public void delete(String product){
        Document document = new Document(NAME,product);
        collection.deleteOne(document);
    }
    public static void main(String[] args) {
        ProductUtility.INSTANCE.insert("ANV");
        System.out.println(ProductUtility.INSTANCE.getAll());
    }
}
