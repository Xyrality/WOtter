package com.xyrality.wotter.eo.migrations;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;

import er.extensions.migration.ERXMigrationDatabase;
import er.extensions.migration.ERXMigrationTable;
import er.extensions.migration.ERXModelVersion;

public class WOtterModel0 extends ERXMigrationDatabase.Migration {
	@Override
	public NSArray<ERXModelVersion> modelDependencies() {
		return null;
	}
  
	@Override
	public void downgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		// DO NOTHING
	}

	@Override
	public void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		ERXMigrationTable accountTable = database.newTableNamed("Account");
		accountTable.newIntegerColumn("id", false);
		accountTable.newStringColumn("name", 255, false);
		accountTable.create();
	 	accountTable.setPrimaryKey("id");

		ERXMigrationTable postTable = database.newTableNamed("Post");
		postTable.newIntegerColumn("accountID", false);
		postTable.newStringColumn("content", 255, false);
		postTable.newIntegerColumn("id", false);
		postTable.newTimestampColumn("postedAt", false);
		postTable.create();
	 	postTable.setPrimaryKey("id");

		postTable.addForeignKey("accountID", "Account", "id");
	}
}