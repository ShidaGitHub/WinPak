package ru.it_mm.winpak.utils.entity;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import ru.it_mm.winpak.utils.entity.CardHolder;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-08-05T12:52:13")
@StaticMetamodel(Card.class)
public class Card_ { 

    public static volatile SingularAttribute<Card, Integer> recordID;
    public static volatile SingularAttribute<Card, Date> timeStamp;
    public static volatile SingularAttribute<Card, Short> deleted;
    public static volatile SingularAttribute<Card, CardHolder> cardHolder;
    public static volatile SingularAttribute<Card, Date> lastReaderDate;
    public static volatile SingularAttribute<Card, Date> activationDate;
    public static volatile SingularAttribute<Card, String> cardNumber;
    public static volatile SingularAttribute<Card, Short> cardStatus;
    public static volatile SingularAttribute<Card, Date> expirationDate;

}