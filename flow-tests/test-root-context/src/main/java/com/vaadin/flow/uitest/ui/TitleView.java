package com.vaadin.flow.uitest.ui;

import com.vaadin.router.Title;
import com.vaadin.ui.html.Div;

@Title("Title view")
public class TitleView extends AbstractDivView {

    @Override
    protected void onShow() {
        removeAll();
        Div titleView = new Div();
        titleView.setText("Title view");
        titleView.setId("annotated");
        add(titleView);
    }

}
