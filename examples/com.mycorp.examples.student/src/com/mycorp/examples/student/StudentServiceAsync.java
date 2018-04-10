/*******************************************************************************
* Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package com.mycorp.examples.student;

import java.util.concurrent.CompletableFuture;

public interface StudentServiceAsync {

	CompletableFuture<Students> getStudentsAsync();

	CompletableFuture<Student> getStudentAsync(String id);

	CompletableFuture<Student> createStudentAsync(String studentName);

	CompletableFuture<Student> updateStudentAsync(Student student);

	CompletableFuture<Student> deleteStudentAsync(String studentId);
}
