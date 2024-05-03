package com.ddb;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

/**
 * Delete a list of tables, handy in testing and debugging.
 *
 * Credentials are read from ~/.aws/credentials.
 * See http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html for more
 * details.
 */


public class App 
{
    private DynamoDB db;

    public App(DynamoDB db) {
        this.db = db;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.printf("Usage: java App <table1> <table2> ... <tableN>\n");
            System.exit(1);
        }
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withCredentials(new ProfileCredentialsProvider())
            .withRegion(Regions.US_WEST_2)
            .build();
        DynamoDB db = new DynamoDB(client);
        new App(db).deleteTables(args);
    }

    public void deleteTables(String[] tableNames) {
        try {
            for (String tableName : tableNames) {
                deleteTable(tableName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteTable(String tableName) {
        Table table = db.getTable(tableName);
        try {
            System.out.printf("Deleting table %s...", tableName);
            table.delete();
            table.waitForDelete();
            System.out.printf(" done!\n");

        } catch (Exception e) {
            System.err.printf("Failed to delete table %s", tableName);
            e.printStackTrace();
        }
    }
}
