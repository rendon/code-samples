package com.ddb;

import java.util.Iterator;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;

/**
 * Truncates table, handy in testing and debugging.
 *
 * Credentials are read from ~/.aws/credentials.
 * See http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html for more
 * details.
 */
public class App {
    private DynamoDB db;

    public App(DynamoDB db) {
        this.db = db;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.printf("Usage: java App <table:partition_key[:sort_key]>\n");
            System.exit(1);
        }
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withCredentials(new ProfileCredentialsProvider())
            .withRegion(Regions.US_WEST_2)
            .build();
        DynamoDB db = new DynamoDB(client);
        new App(db).truncateTable(args[0]);
    }

    public void truncateTable(String tableSpec) {
        try {
            String[] tokens = tableSpec.split(":");
            if (tokens.length == 2) {
                truncateTable(tokens[0], tokens[1]);
            } else if (tokens.length == 3) {
                truncateTable(tokens[0], tokens[1], tokens[2]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void truncateTable(String tableName, String partitionKeyName, String sortKeyName) throws Exception {
        Table table = db.getTable(tableName);
        ScanSpec spec = new ScanSpec();
        ItemCollection<ScanOutcome> items = table.scan(spec);
        Iterator<Item> it = items.iterator();
        while (it.hasNext()) {
            Item item = it.next();
            String partitionKey = item.getString(partitionKeyName);
            String sortKey = item.getString(sortKeyName);
            PrimaryKey key = new PrimaryKey( partitionKeyName, partitionKey, sortKeyName, sortKey);
            table.deleteItem(key);
            System.out.printf("Deleted item with key: <%s, %s>\n", partitionKey, sortKey);
        }
    }

    private void truncateTable(String tableName, String partitionKeyName) throws Exception {
        Table table = db.getTable(tableName);
        ScanSpec spec = new ScanSpec();
        ItemCollection<ScanOutcome> items = table.scan(spec);
        Iterator<Item> it = items.iterator();
        while (it.hasNext()) {
            Item item = it.next();
            String partitionKey = item.getString(partitionKeyName);
            PrimaryKey key = new PrimaryKey(partitionKeyName, partitionKey);
            table.deleteItem(key);
            System.out.printf("Deleted item with key: %s\n", partitionKey);
        }
    }
}
