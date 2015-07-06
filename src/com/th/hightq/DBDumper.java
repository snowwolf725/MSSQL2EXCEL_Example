package com.th.hightq;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBDumper {
	
	private static List<String> m_itemIds = new ArrayList<String>();
	
	private static Map<String, Customer> m_map_customers = new HashMap<String, Customer>();
	
	private static List<History> m_histories = new ArrayList<History>();
	
	private static long customerCount = 0;
	
	private static long historyCount = 0;
	
	public static void main(String[] args) {
		try {
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
			String connUrl = "jdbc:jtds:sqlserver://192.168.0.122:1433;databaseName=HIGHQ";
			Connection conn = DriverManager.getConnection(connUrl, "HIGHQ", "HIGHQ");
			Statement stmt = conn.createStatement();
			
			String str_customerQuerySQL = "select * from v_Object";
			ResultSet rs = stmt.executeQuery(str_customerQuerySQL);
			while(rs.next()) {
				Customer customer = m_map_customers.get(rs.getString("ObjNo"));
				if(customer == null) {
					customer = new Customer();
					customer.setCustomerId(rs.getString("ObjNo"));
					customer.setCustomerName(rs.getString("Name"));
					customer.setCustomerAddr(rs.getString("Addr"));
					customer.setCustomerTel(rs.getString("Tel"));
					m_map_customers.put(rs.getString("ObjNo"), customer);
				}
			}
			rs.close();
			for(String customerId : m_map_customers.keySet()) {
				Customer customer = m_map_customers.get(customerId);
				customerCount++;
				if(customerCount%10 == 0) {
					System.out.println(customerCount + "/" +  + m_map_customers.size() + "/" + m_histories.size() + "/" + historyCount);
				}
				if(m_histories.size() > 20000) {
					historyCount += m_histories.size();
					saveHistory();
					m_histories.clear();
				}
				m_itemIds.clear();
				String str_itemQuerySQL = "select distinct bj02,bj031,bj032,da04,A=bj02 from tnm02CustItemtbm10 left join tdm01 on bj02=da01 " + 
								 " where bj010='C' and bj011='" + customerId + "' AND (1=1) AND  1=1  order by A,bj02";
				rs = stmt.executeQuery(str_itemQuerySQL);
				while(rs.next()) {
					m_itemIds.add(rs.getString("bj02"));
				}
				rs.close();
				if(m_itemIds.size() <= 0) {
					continue;
				}
				for(String itemId : m_itemIds) {
					String str_itemDetailQuerySQL = "select na12,na15, na04 ,na030, na031,na03=na030+'-'+na031, " + 
							 "na21, na06, na07, na08, na09,na10, na11, na13, na14, na16, memo=case " +
							"when (na030='3' or na030='4') then (select Hk09 from khm11 where Hk010=na030 and Hk011=na031 and Hk02=na23) " + 
							"else '' end, na17, na05, na23,na24, Hj23=case when (na030='3' or na030='4') " + 
							"Then (select Hj23 from khm10 where Hj010=na030 and Hj011=na031) " + 
							"else '' end,na25,na02,na26, TermPort=(case when na05 in ('FOB','FOB&C','FOR','EXWORK') " +
							"then na19 else na20 end) , type from ( select *, type = case when na030 in ('B', 'O', '3', '4') " +
							"then '1' else '0' end from tnm01 union select * from v_234Item_History )a " +
							"where na011='" + customerId + "' and na02= '" + itemId + "' and na010='C'  and " + 
							"( (na030 in ('3','4')) or (na030 not in ('Q','S','B','O','3','4','I')) ) and ( na26 in ('B') ) and " + 
							"type in ('0', '1','2','3','4') and  1=1  order by na04 desc,na030 desc,na031 desc";
					rs = stmt.executeQuery(str_itemDetailQuerySQL);
					while(rs.next()) {
						History history = new History();
						history.setCustomer(customer);
						history.setDate(rs.getString("na04"));
						history.setTicketId(rs.getString("na031"));
						history.setMoneyType(rs.getString("na06"));
						history.setPrice(rs.getLong("na07"));
						history.setUnit(rs.getString("na08"));
						history.setAmount(rs.getLong("na09"));
						history.setRate(rs.getDouble("na11"));
						history.setDiscount(rs.getLong("na13"));
						history.setItemId(itemId);
						history.setItemName(rs.getString("memo"));
						m_histories.add(history);
					}
					rs.close();
				}
			}
			
			// save final data
			historyCount += m_histories.size();
			saveHistory();
			m_histories.clear();
			System.out.println(customerCount + "/" +  + m_map_customers.size() + "/" + m_histories.size() + "/" + historyCount);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} finally {
			
		}
	}
	
	public static void saveHistory() {
		List<PrintableDataItem> items = new ArrayList<PrintableDataItem>();
		PrintableDataItem dataItem = null;
		int rowIndex = 6;
		for(History history : m_histories) {
			dataItem = new PrintableDataItem(rowIndex, 8, history.getCustomer().getCustomerId());
			items.add(dataItem);
			dataItem = new PrintableDataItem(rowIndex, 26, history.getCustomer().getCustomerName());
			items.add(dataItem);
			dataItem = new PrintableDataItem(rowIndex, 27, history.getCustomer().getCustomerAddr());
			items.add(dataItem);
			dataItem = new PrintableDataItem(rowIndex, 28, history.getCustomer().getCustomerZipCode());
			items.add(dataItem);
			dataItem = new PrintableDataItem(rowIndex, 29, history.getCustomer().getCustomerTel());
			items.add(dataItem);
			dataItem = new PrintableDataItem(rowIndex, 9, history.getItemId());
			items.add(dataItem);
			dataItem = new PrintableDataItem(rowIndex, 30, history.getItemName());
			items.add(dataItem);
			dataItem = new PrintableDataItem(rowIndex, 16, history.getDate());
			items.add(dataItem);
			dataItem = new PrintableDataItem(rowIndex, 31, history.getTicketId());
			items.add(dataItem);
			dataItem = new PrintableDataItem(rowIndex, 11, history.getMoneyType());
			items.add(dataItem);
			dataItem = new PrintableDataItem(rowIndex, 14, history.getPrice() + "");
			items.add(dataItem);
			dataItem = new PrintableDataItem(rowIndex, 10, history.getUnit());
			items.add(dataItem);
			dataItem = new PrintableDataItem(rowIndex, 32, history.getAmount() + "");
			items.add(dataItem);
			dataItem = new PrintableDataItem(rowIndex, 33, history.getRate() + "");
			items.add(dataItem);
			dataItem = new PrintableDataItem(rowIndex, 34, history.getDiscount() + "");
			items.add(dataItem);
			rowIndex++;
		}
		ReportGenerator generator = new ReportGenerator();
		generator.setPrintInfo("Result", "Result" + historyCount, items);
		generator.genReport();
	}
}
