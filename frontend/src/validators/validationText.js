// returns text validation messages for Form fields used throughout Blogen
// Vuelidate is used as the validation library so all input parameters to each method expect a vuelidate object

export function emailValidText (email) {
  return email.email ? 'Looks Good' : ''
}

export function emailInvalidText (email) {
  if (email.$dirty) {
    if (!email.required) {
      return 'email is required'
    } else if (!email.email) {
      return 'Please enter a valid email address'
    } else if (!email.isUnique) {
      return 'email address is already taken'
    }
  } else {
    return ''
  }
}

export function passwordValidText (password) {
  return !password.$error ? 'OK!' : ''
}

export function passwordInvalidText (password) {
  if (password.$dirty) {
    if (!password.required) {
      return 'password is required'
    } else if (!password.hasUpperCase) {
      return 'password must contain one upper case letter'
    } else if (!password.minLength) {
      return `password must have at least ${password.$params.minLength.min} characters`
    }
  } else {
    return ''
  }
}

export function confPasswordValidText (confPassword) {
  return !confPassword.$error ? 'Ok' : ''
}

export function confPasswordInvalidText (confPassword) {
  if (confPassword.$dirty) {
    if (!confPassword.sameAs.sameAsPassword) {
      return 'passwords must match'
    }
  } else {
    return ''
  }
}

export function preferredUsernameValidText (pu) {
  return !pu.$error ? 'Good' : ''
}

export function preferredUsernameInvalidText (pu) {
  if (pu.$dirty) {
    if (!pu.minLength) {
      return `preferred username must have at least ${pu.$params.minLength.min} characters`
    } else if (!pu.alphaNum) {
      return `preferred username must contain only alpha-numeric characters`
    } else if (!pu.required) {
      return 'preferred username is required'
    } else if (!pu.isUnique) {
      return 'preferred username is already taken'
    }
  } else {
    return ''
  }
}

export function firstNameValidText (firstName) {
  return !firstName.$error ? 'Looks Good' : ''
}

export function firstNameInvalidText (firstName) {
  if (firstName.$dirty) {
    if (!firstName.required) {
      return 'first name is required'
    }
  } else {
    return ''
  }
}

export function lastNameValidText (lastName) {
  return !lastName.$error ? 'Looks Good' : ''
}

export function lastNameInvalidText (lastName) {
  if (lastName.$dirty) {
    if (!lastName.required) {
      return 'last name is required'
    }
  } else {
    return ''
  }
}

export function confCodeValidText (cc) {
  return !cc.$error ? '' : ''
}

export function confCodeInvalidText (cc) {
  if (cc.$dirty) {
    if (!cc.required) {
      return 'confirmation code is required'
    }
  } else {
    return ''
  }
}

export function requiredFieldValidText (field, text = '') {
  return !field.$error ? text : ''
}

export function requiredFieldInvalidText (field, text = 'required') {
  if (field.$dirty) {
    if (!field.required) {
      return text
    }
  } else {
    return ''
  }
}
