package org.communitybridge.dao;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static org.communitybridge.dao.WebGroupDao.EMPTY_LIST;
import org.communitybridge.main.Configuration;
import org.communitybridge.main.SQL;
import org.communitybridge.utility.Log;

public class KeyValueWebGroupDao extends WebGroupDao
{
	public static final String EXCEPTION_MESSAGE_GETSECONDARY = "Exception during KeyValueWebGroupDao.getSecondaryGroups(): ";

	public KeyValueWebGroupDao(Configuration configuration, SQL sql, Log log)
	{
		super(configuration, sql, log);
	}

	@Override
	public List<String> getUserSecondaryGroupIDs(String userID)
	{
		List<String> groupIDs = new ArrayList<String>();
		String query =
						"SELECT `" + configuration.webappSecondaryGroupGroupIDColumn + "` "
					+ "FROM `" + configuration.webappSecondaryGroupTable + "` "
					+ "WHERE `" + configuration.webappSecondaryGroupUserIDColumn + "` = '" + userID + "' "
					+ "AND `" + configuration.webappSecondaryGroupKeyColumn + "` = '" + configuration.webappSecondaryGroupKeyName + "' ";

		try
		{
			result = sql.sqlQuery(query);

			if (result.next())
			{
				return convertDelimitedIDString(result.getString(configuration.webappSecondaryGroupGroupIDColumn));
			}
			return groupIDs;
		}
		catch (SQLException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETSECONDARY + exception.getMessage());
			return EMPTY_LIST;
		}
		catch (MalformedURLException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETSECONDARY + exception.getMessage());
			return EMPTY_LIST;
		}
		catch (InstantiationException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETSECONDARY + exception.getMessage());
			return EMPTY_LIST;
		}
		catch (IllegalAccessException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETSECONDARY + exception.getMessage());
			return EMPTY_LIST;
		}
	}

	@Override
	public List<String> getGroupUserIDs(String groupID)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<String> getGroupUserIDsPrimary(String groupID)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<String> getGroupUserIDsSecondary(String groupID)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
