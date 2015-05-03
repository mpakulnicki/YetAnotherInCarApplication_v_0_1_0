package marcinpakulnicki.net.yetanotherincarapplication.events;

public interface YaicaEvent {
    /**
     * Method returns human readable description of the event
     * @return Description of the event
     */

    String getFadeOutCardName();
    String setFadeOutCardName(String inFadeOutCardName);
    String getFadeInCardName();
    String setFadeInCardName(String inFadeInCardName);


}