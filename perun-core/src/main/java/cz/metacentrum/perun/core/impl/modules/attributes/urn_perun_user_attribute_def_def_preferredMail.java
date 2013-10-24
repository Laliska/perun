package cz.metacentrum.perun.core.impl.modules.attributes;

import java.util.List;

import cz.metacentrum.perun.core.api.Attribute;
import cz.metacentrum.perun.core.api.AttributeDefinition;
import cz.metacentrum.perun.core.api.AttributesManager;
import cz.metacentrum.perun.core.api.Member;
import cz.metacentrum.perun.core.api.User;
import cz.metacentrum.perun.core.api.exceptions.AttributeNotExistsException;
import cz.metacentrum.perun.core.api.exceptions.ConsistencyErrorException;
import cz.metacentrum.perun.core.api.exceptions.InternalErrorException;
import cz.metacentrum.perun.core.api.exceptions.WrongAttributeAssignmentException;
import cz.metacentrum.perun.core.api.exceptions.WrongAttributeValueException;
import cz.metacentrum.perun.core.api.exceptions.WrongReferenceAttributeValueException;
import cz.metacentrum.perun.core.impl.PerunSessionImpl;
import cz.metacentrum.perun.core.implApi.modules.attributes.UserAttributesModuleAbstract;
import cz.metacentrum.perun.core.implApi.modules.attributes.UserAttributesModuleImplApi;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michal Šťava   <stava.michal@gmail.com>
 * @version $Id$
 */
public class urn_perun_user_attribute_def_def_preferredMail extends UserAttributesModuleAbstract implements UserAttributesModuleImplApi {

  private static Pattern emailPattern = Pattern.compile("^[-_A-Za-z0-9+]+(\\.[-_A-Za-z0-9]+)*@[-A-Za-z0-9]+(\\.[-A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
  private static final String A_M_mail = AttributesManager.NS_MEMBER_ATTR_DEF + ":mail";
  
  public void checkAttributeValue(PerunSessionImpl sess, User user, Attribute attribute) throws InternalErrorException, WrongAttributeValueException, WrongAttributeAssignmentException, WrongReferenceAttributeValueException {
    String attributeValue = null;
        
    if(attribute.getValue() == null) throw new WrongAttributeValueException(attribute, user, "User preferred mail can't be set to null.");
    else attributeValue = (String) attribute.getValue();
        
    Matcher emailMatcher = emailPattern.matcher(attributeValue);
    if(!emailMatcher.find()) throw new WrongAttributeValueException(attribute, user, "Email is not in correct form.");  
    
    /* User preferredMail now can be anything
    //user prefferedMail can be only one of memberMails if any
    List<Member> membersOfUser = sess.getPerunBl().getMembersManagerBl().getMembersByUser(sess, user);
    StringBuilder possiblePrefferedMailValues = new StringBuilder();
    for(Member m: membersOfUser) {
        Attribute memberMail = null;
        try {
            memberMail = sess.getPerunBl().getAttributesManagerBl().getAttribute(sess, m, A_M_mail);
        } catch (AttributeNotExistsException ex) {
            throw new ConsistencyErrorException(ex);
        } catch (WrongAttributeAssignmentException ex) {
            throw new InternalErrorException(ex);
        }
        
        if(attribute.getValue().equals(memberMail.getValue())) return;
        if(memberMail != null) {
            if(possiblePrefferedMailValues.length() != 0) possiblePrefferedMailValues.append(", ");
            possiblePrefferedMailValues.append("'" + memberMail.getValue() + "'");
        }
    }
    throw new WrongAttributeValueException("Attribute user preffered mail can be null (if no members mail exists) or one of the existing member's mails [" + possiblePrefferedMailValues.toString() + "]. " + attribute);
    */
  }

  /* Not needed now this funcionality
  @Override
  public void changedAttributeHook(PerunSessionImpl session, User user, Attribute attribute) throws InternalErrorException, WrongReferenceAttributeValueException {
    //If this is removing and userPrefferedMail is going to be null, try to get one of the member mail and set it to user Preffered Mail
    if(attribute.getValue() == null) {
        List<Member> membersOfUser = session.getPerunBl().getMembersManagerBl().getMembersByUser(session, user);
        
        String anyNotNullMemberMail = null;
        Attribute memberMail = null;
        for(Member m: membersOfUser) {
            try {
                memberMail = session.getPerunBl().getAttributesManagerBl().getAttribute(session, m, A_M_mail);
            } catch (AttributeNotExistsException ex) {
                throw new ConsistencyErrorException(ex);
            } catch (WrongAttributeAssignmentException ex) {
                throw new InternalErrorException(ex);
            }
            if(memberMail.getValue() != null) {
                anyNotNullMemberMail = (String) memberMail.getValue();
                break;
            }
        }
        
        //if there is no notNull memberMail so this email can't be set
        if(anyNotNullMemberMail == null) return;
        else {
            attribute.setValue(anyNotNullMemberMail);
            try {
                session.getPerunBl().getAttributesManagerBl().setAttribute(session, user, attribute);
            } catch (WrongAttributeValueException ex) {
                throw new WrongReferenceAttributeValueException(attribute, memberMail, "Member mail is not in correct form to save it into user prefferedMail.", ex);
            } catch (WrongAttributeAssignmentException ex) {
                throw new InternalErrorException(ex);
            }
        }
    }
  }*/ 
  
  public AttributeDefinition getAttributeDefinition() {
      AttributeDefinition attr = new AttributeDefinition();
      attr.setNamespace(AttributesManager.NS_USER_ATTR_DEF);
      attr.setFriendlyName("preferredMail");
      attr.setType(String.class.getName());
      attr.setDescription("User's preferred mail");
      return attr;
  }
}