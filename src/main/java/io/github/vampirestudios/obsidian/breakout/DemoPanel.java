package io.github.vampirestudios.obsidian.breakout;

import net.minecraft.util.Identifier;
import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.FlexPanel;
import org.liquidengine.legui.component.ImageView;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.component.PasswordInput;
import org.liquidengine.legui.component.RadioButton;
import org.liquidengine.legui.component.RadioButtonGroup;
import org.liquidengine.legui.component.TextArea;
import org.liquidengine.legui.component.TextInput;
import org.liquidengine.legui.component.Widget;
import org.liquidengine.legui.component.optional.TextState;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.component.optional.align.VerticalAlign;
import org.liquidengine.legui.image.loader.ImageLoader;
import org.liquidengine.legui.style.Style.DisplayType;
import org.liquidengine.legui.style.flex.FlexStyle.AlignItems;
import org.liquidengine.legui.style.flex.FlexStyle.FlexDirection;
import org.liquidengine.legui.style.flex.FlexStyle.JustifyContent;

public class DemoPanel extends Panel {
    public DemoPanel(int width, int height) {
        super(0.0F, 0.0F, width, height);
        Label title = new Label("Generator", 100, 10.0F, 100.0F, 30.0F);
        title.getStyle().setTextColor(0.0F, 0.5F, 1.0F, 1.0F).setFontSize(30.0F).setFont("roboto-bold");
        this.add(title);
        TextArea desc = new TextArea(10.0F, 45.0F, 400.0F, 160.0F);
        desc.setTextState(new TextState("This GUI is powered by the LEGUI library. It's being rendered with the\nsame OpenGL Context as the Minecraft game window, so it has access\nto all the games resources and data.\n\nThis window can also be used for any number of other purposes. It is\nfully integrated into the game, so a portion of the Minecraft world can\nbe rendered here, or even a map, or a preview of the players inventory."));
        desc.setHorizontalScrollBarVisible(false).setVerticalScrollBarVisible(false);
        desc.getStyle().setVerticalAlign(VerticalAlign.TOP).setBorderRadius(0.0F);
        this.add(desc);
        Widget radioDemo = new Widget("Demo Widget", 100.0F, 200.0F, 100.0F, 100.0F);
        Label innerText = new Label("Here are some radio buttons for your enjoyment:", 10.0F, 10.0F, 100.0F, 20.0F);
        radioDemo.getContainer().add(innerText);
        RadioButtonGroup radioButtons = new RadioButtonGroup();
        RadioButton radio1 = (new RadioButton("First Option", 10.0F, 40.0F, 100.0F, 20.0F)).setChecked(true);
        RadioButton radio2 = new RadioButton("Second Option", 10.0F, 70.0F, 100.0F, 20.0F);
        radio1.setRadioButtonGroup(radioButtons);
        radio2.setRadioButtonGroup(radioButtons);
        radioDemo.getContainer().add(radio1).add(radio2);
        this.add(radioDemo);
        Widget textInputDemo = new Widget("Text Input Demo", 200.0F, 500.0F, 300.0F, 250.0F);
        textInputDemo.setResizable(false);
        Label innerText2 = new Label("Login Form", 0.0F, 10.0F, 300.0F, 20.0F);
        innerText2.getStyle().setTextColor(1.0F, 0.5F, 0.0F, 1.0F).setHorizontalAlign(HorizontalAlign.CENTER);
        innerText2.getStyle().setFont("roboto-bold").setFontSize(30.0F);
        textInputDemo.getContainer().add(innerText2);
        Label usernameLabel = new Label("Username", 10.0F, 50.0F, 280.0F, 20.0F);
        textInputDemo.getContainer().add(usernameLabel);
        TextInput usernameInput = new TextInput("Username", 10.0F, 70.0F, 280.0F, 30.0F);
        usernameInput.getStyle().setFontSize(20.0F).setVerticalAlign(VerticalAlign.MIDDLE);
        textInputDemo.getContainer().add(usernameInput);
        Label two = new Label("Password", 10.0F, 110.0F, 280.0F, 20.0F);
        textInputDemo.getContainer().add(two);
        PasswordInput passwordInput = new PasswordInput("Password", 10.0F, 130.0F, 280.0F, 30.0F);
        passwordInput.getStyle().setFontSize(20.0F).setVerticalAlign(VerticalAlign.MIDDLE);
        textInputDemo.getContainer().add(passwordInput);
        Label left = new Label("Forgot password?", 10.0F, 160.0F, 280.0F, 20.0F);
        left.getStyle().setTextColor(0.30078125F, 0.64453125F, 0.9296875F, 1.0F);
        left.getHoveredStyle().setTextColor(0.53515625F, 0.76171875F, 0.9453125F, 1.0F);
        textInputDemo.getContainer().add(left);
        Button loginButton = new Button("Login", 10.0F, 190.0F, 280.0F, 30.0F);
        loginButton.getStyle().setFont("roboto-bold").setFontSize(20.0F);
        loginButton.getStyle().setHorizontalAlign(HorizontalAlign.CENTER).setVerticalAlign(VerticalAlign.MIDDLE);
        loginButton.getStyle().setTextColor(1.0F, 1.0F, 1.0F, 1.0F);
        loginButton.getStyle().getBackground().setColor(0.07421875F, 0.359375F, 0.921875F, 1.0F);
        loginButton.getHoveredStyle().getBackground().setColor(0.04296875F, 0.23828125F, 0.62890625F, 1.0F);
        textInputDemo.getContainer().add(loginButton);
        this.add(textInputDemo);
        Widget imageWrapper = new Widget(20.0F, 30.0F, 100.0F, 100.0F);
        imageWrapper.setAscendible(true);
        imageWrapper.setTitleEnabled(true);
        ImageView imageView = new ImageView(ImageLoader.loadImage(new Identifier("textures/gui/container/furnace.png")));
        imageView.setPosition(15.0F, 5.0F).setSize(256.0F, 256.0F).getStyle().setBorderRadius(10.0F);
        imageWrapper.getTitle().getTextState().setText("Ascendible widget");
        imageWrapper.getContainer().add(imageView);
        imageWrapper.setCloseable(false);
        this.add(imageWrapper);
        Widget flexDemo = new Widget("Flex Layout Demo", 150.0F, 400.0F, 300.0F, 150.0F);
        flexDemo.getContainer().getStyle().setDisplay(DisplayType.FLEX);
        flexDemo.getContainer().getFlexStyle().setFlexDirection(FlexDirection.COLUMN);
        Label one = new Label("Hello Flex World");
        one.getStyle().enableFlex(200.0F, 30.0F).setFont("roboto-bold").setFontSize(25.0F);
        flexDemo.getContainer().add(one);
        two = new Label("this is a description");
        two.getStyle().enableFlex(200.0F, 20.0F).setTextColor(1.0F, 0.5F, 0.0F, 1.0F);
        flexDemo.getContainer().add(two);
        FlexPanel across = new FlexPanel(300.0F, 50.0F);
        across.getStyle().getBackground().setColor(0.0F, 1.0F, 0.0F, 0.2F);
        across.getFlexStyle().setAlignItems(AlignItems.CENTER).setJustifyContent(JustifyContent.CENTER);
        left = new Label("Name");
        left.getStyle().enableFlex(60.0F, 35.0F).setFontSize(20.0F).setHorizontalAlign(HorizontalAlign.RIGHT).setPadding(2.5F);
        across.add(left);
        TextInput right = new TextInput();
        right.getStyle().enableFlex(140.0F, 35.0F).setFontSize(20.0F).setPadding(2.5F);
        across.add(right);
        flexDemo.getContainer().add(across);
        this.add(flexDemo);
    }
}
