package com.andcopro.bean;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;

import org.compiere.model.MBPartner;
import org.compiere.model.MRole;
import org.compiere.model.MUser;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;

import com.andcopro.util.EmailValidator;

public class SessionUser implements WUser {
	
	
    public static final Set<String> USER_GAS = new HashSet<String>();
    static {
    	USER_GAS.add("ROLE_USER");
    }
    
	
	private int AD_User_ID = 0;
	private int C_BPartner_ID = 0;
	private int M_PriceList_ID = 0;
	private int C_BP_Group_ID = 0;
    private String username = null;
    private String email;
    private String password;
    
    private boolean enabled;
    private boolean accountNonLocked;
    
    private MBPartner bpartner;
    private MUser user;  
    private String value;
    private String salt;
    
    
    private Set<String> authorities;


	private CLogger log = CLogger.getCLogger(getClass().getName());


	public SessionUser() {
		// TODO Auto-generated constructor stub
	}	
	
	
	public SessionUser(Properties ctx, String login) {
			log.info(login);

			String sql = "SELECT u.AD_User_ID, u.value, u.email, u.password, u.isActive, bp.isActive, bp.SOCreditStatus," +
					"bp.C_BPartner_ID, bp.M_PriceList_ID, bp.C_BP_Group_ID, u.Description, u.name, u.salt, u.isLocked"
					+ " FROM AD_User u " +
				"INNER JOIN C_BPartner bp ON bp.C_BPartner_ID = u.C_BPartner_ID " +
				"WHERE u.isActive='Y' AND %s LIKE trim(?) ";
			if (EmailValidator.validate(login))
				sql = String.format(sql, "u.EMail");
			else
				sql = String.format(sql, "u.Value");	
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql, null);
				
				pstmt.setString(1, login.trim());
				rs = pstmt.executeQuery();
				if (rs.next())
				{
					AD_User_ID = rs.getInt(1);
					C_BPartner_ID = rs.getInt(8);
					M_PriceList_ID = rs.getInt(9);
					C_BP_Group_ID = rs.getInt(10);
					value = rs.getString(2);
					username = rs.getString(12);
					salt = rs.getString(13);
					email = rs.getString(3);
					password = rs.getString(4);
//					accountNonLocked = !rs.getString(7).equals(MBPartner.SOCREDITSTATUS_CreditStop);
					accountNonLocked = !rs.getString(7).equals(MBPartner.SOCREDITSTATUS_CreditStop) 
							&& rs.getString(14).equals("N");
					
//					password = getHash(rs.getString(4), rs.getString(13));
					
					
					user = MUser.get(ctx, AD_User_ID);
					bpartner = MBPartner.get(ctx, rs.getInt(8));
				
				MRole[] roles = user.getRoles(Env.getAD_Org_ID(ctx));

				authorities = new HashSet<String>();
//				if (roles.length == 0)
//					authorities.add("ROLE_USER");
//				else {
					for (MRole role : roles) {
						authorities.add("ROLE_"+role.getAD_Role_ID());
					}
//				}
				authorities.add("ROLE_USER");
				authorities = Collections.unmodifiableSet(sortAuthorities(authorities));
				
				enabled = rs.getString(5).equals("Y") & rs.getString(6).equals("Y");

/*				
				boolean canLogin = false;
				for (MStoreBPartnerGroup storeGroup : webStore.getMStoreBPartnerGroups(false)) {
					if (storeGroup.getC_BP_Group_ID() == C_BP_Group_ID) {
						canLogin = true;
						break;
					}
				}
				if (!canLogin) {
					log.warning("ACCESS DENIED WebStore : "+webStore.getName()+", user : "+getUsername()+", userGroup : "+MBPGroup.get(ctx, C_BP_Group_ID).getName());
					enabled = false;
				}					
*/				
				
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}


	}	//	load
	


	@Override
	public String getUsername() {
		return username;
	}
	
	public String getPassword()
	{
		return password;
	}


	@Override
	public Collection<String> getAuthorities() {
		return authorities;
	}



	@Override
	public boolean isAccountNonExpired() {
		return true;
	}




	@Override
	public boolean isAccountNonLocked() {
		
		return accountNonLocked;
	}



	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}


	@Override
	public boolean isEnabled() {
		
		return enabled;
	}	


	public void eraseCredentials() {
		
		password = null;
		
	}	

    @Override
    public boolean equals(Object rhs) {
        if (rhs instanceof SessionUser) {
            return username.equals(((SessionUser) rhs).username);
        }
        return false;
    }

    /**
     * Returns the hashcode of the {@code username}.
     */
    @Override
    public int hashCode() {
        return username.hashCode();
    }	

    
    private static SortedSet<String> sortAuthorities(Collection<? extends String> authorities) {
//        Assert.notNull(authorities, "Cannot pass a null String collection");
        // Ensure array iteration order is predictable (as per UserDetails.getAuthorities() contract and SEC-717)
        SortedSet<String> sortedAuthorities =
            new TreeSet<String>(new AuthorityComparator());

        for (String String : authorities) {
 //           Assert.notNull(String, "String list cannot contain any null elements");
            sortedAuthorities.add(String);
        }

        return sortedAuthorities;
    }

    private static class AuthorityComparator implements Comparator<String>, Serializable {

        public int compare(String g1, String g2) {
            // Neither should ever be null as each entry is checked before adding it to the set.
            // If the authority is null, it is a custom authority and should precede others.
            if (g2 == null) {
                return -1;
            }

            if (g1 == null) {
                return 1;
            }

            return g1.compareTo(g2);
        }
    }    
    
      
    
    
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer("SessionUser[id=");
		sb.append(AD_User_ID).append(", name=").append(username)
			.append(", email=").append(email)
			.append("]");
		return sb.toString();
	}	//	toString

	
	public boolean isWebAdmin() {
		
		return (Boolean)bpartner.get_Value("isWebAdmin");
	}
	public boolean isWebManager() {
		
		return (Boolean)bpartner.get_Value("isWebManager");
	}	
	public boolean isWebVIF() {
		
		return (Boolean)bpartner.get_Value("isWebVIF");
	}		
	

	/*
	public MBPartner getBpartner() {
		return bpartner;
	}



	public MUser getUser() {
		return user;
	}
*/
	
	
	public int getSalesRep_ID()
	{
		return bpartner.getSalesRep_ID();
	}
	
	/* (non-Javadoc)
	 * @see com.vif.bean.UserCredential#getEmail()
	 */
	@Override
	public String getEmail() {
		return email;
	}


	public int getC_PaymentTerm_ID()
	{
		return bpartner.getC_PaymentTerm_ID();
	}	

	public int getAD_User_ID() {
		return AD_User_ID;
	}

	@Override
	public int getC_BP_Group_ID() {
		return C_BP_Group_ID;
	}


	public int getC_BPartner_ID() {
		return C_BPartner_ID;
	}



	public int getM_PriceList_ID() {
		return M_PriceList_ID;
	}


	@Override
	public String getSalt() {

		return salt;
	}
    
	
	
	
}	//	WebUser
