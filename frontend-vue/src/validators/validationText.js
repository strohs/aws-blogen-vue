// A collection of functions that can be used to compute messages for use in vuelidate forms.

export function emailValidText(email) {
  return !email.$error ? "Good!" : "";
}

export function emailInvalidText(email) {
  if (email.$error) {
    return email.$errors.map((errObj) => errObj.$message).join(", ");
  } else {
    return "";
  }
}

export function passwordValidText(password) {
  return !password.$error ? "OK!" : "";
}

export function passwordInvalidText(password) {
  if (password.$dirty) {
    if (!password.required) {
      return "password is required";
    } else if (!password.hasUpperCase) {
      return "password must contain one upper case letter";
    } else if (!password.minLength) {
      return `password must have at least ${password.$params.minLength.min} characters`;
    }
  } else {
    return "";
  }
}

export function confPasswordValidText(confPassword) {
  return !confPassword.$error ? "Ok" : "";
}

export function confPasswordInvalidText(confPassword) {
  if (confPassword.$dirty) {
    if (!confPassword.sameAs.sameAsPassword) {
      return "passwords must match";
    }
  } else {
    return "";
  }
}

export function preferredUsernameValidText(pu) {
  return !pu.$error ? "Good" : "";
}

export function preferredUsernameInvalidText(pu) {
  if (pu.$error) {
    return pu.$errors.map((error) => error.$message).join(", ");
  } else {
    return "";
  }
}

export function firstNameValidText(firstName) {
  return !firstName.$error ? "Looks Good" : "";
}

export function firstNameInvalidText(firstName) {
  if (firstName.$error) {
    return firstName.$errors.map((errObj) => errObj.$message).join(", ");
  } else {
    return "";
  }
}

export function lastNameValidText(lastName) {
  return !lastName.$error ? "Looks Good" : "";
}

export function lastNameInvalidText(lastName) {
  if (lastName.$error) {
    return lastName.$errors.map((errObj) => errObj.$message).join(", ");
  } else {
    return "";
  }
}

export function confCodeValidText(cc) {
  return !cc.$error ? "" : "";
}

export function confCodeInvalidText(cc) {
  if (cc.$dirty) {
    if (!cc.required) {
      return "confirmation code is required";
    }
  } else {
    return "";
  }
}

export function requiredFieldValidText(field, text = "") {
  return !field.$error ? text : "";
}

export function requiredFieldInvalidText(field, text = "required") {
  if (field.$dirty) {
    if (!field.required) {
      return text;
    }
  } else {
    return "";
  }
}
