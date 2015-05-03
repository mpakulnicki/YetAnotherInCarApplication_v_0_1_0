package marcinpakulnicki.net.yetanotherincarapplication.events;


public class FragmentGuiAlphaTweenEvent implements YaicaEvent {

    private String fadeInCardName;
    private String fadeOutCardName;

    public FragmentGuiAlphaTweenEvent() {
        super();
    }

    @Override
    public String getFadeOutCardName() {
        return fadeOutCardName;
    }

    @Override
    public String setFadeOutCardName(String inFadeOutCardName) {
        fadeOutCardName = inFadeOutCardName;
        return fadeOutCardName;
    }

    @Override
    public String getFadeInCardName() {
        return fadeInCardName;
    }

    @Override
    public String setFadeInCardName(String inFadeInCardName) {
        fadeInCardName = inFadeInCardName;
        return fadeInCardName;
    }
}
