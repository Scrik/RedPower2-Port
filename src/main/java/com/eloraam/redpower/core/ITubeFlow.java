package com.eloraam.redpower.core;

import com.eloraam.redpower.core.ITubeConnectable;
import com.eloraam.redpower.core.TubeFlow;
import com.eloraam.redpower.core.TubeItem;

public interface ITubeFlow extends ITubeConnectable {

   void addTubeItem(TubeItem var1);

   TubeFlow getTubeFlow();
}
