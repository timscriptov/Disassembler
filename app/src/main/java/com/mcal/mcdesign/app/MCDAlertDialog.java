/*
 * Copyright (C) 2018-2019 Тимашков Иван
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mcal.mcdesign.app;

//##################################################################
/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class MCDAlertDialog extends android.support.v7.app.AlertDialog
//##################################################################
{
	protected MCDAlertDialog(android.content.Context context)
	{
		super(context, android.R.style.Theme_Translucent);
	}

	public static class Builder extends android.support.v7.app.AlertDialog.Builder
	{
		public Builder(android.content.Context context) 
		{
			super(context);
		}

        public Builder(android.content.Context context, int themeResId)
		{
			super(context, themeResId);
		}
	}
}
