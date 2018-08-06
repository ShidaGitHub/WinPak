package ru.it_mm.winpak.utils.entity;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import ru.it_mm.winpak.utils.entity.Card;
import ru.it_mm.winpak.utils.entity.CardHolder;
import ru.it_mm.winpak.utils.entity.HWIndependentDevices;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-08-05T12:52:13")
@StaticMetamodel(History.class)
public class History_ { 

    public static volatile SingularAttribute<History, Integer> recordID;
    public static volatile SingularAttribute<History, Date> timeStamp;
    public static volatile SingularAttribute<History, Short> deleted;
    public static volatile SingularAttribute<History, CardHolder> cardHolder;
    public static volatile SingularAttribute<History, HWIndependentDevices> hWIndependentDevices;
    public static volatile SingularAttribute<History, Date> genTime;
    public static volatile SingularAttribute<History, String> param3;
    public static volatile SingularAttribute<History, Integer> param1;
    public static volatile SingularAttribute<History, Card> card;

}