import webapp2
import logging
from google.appengine.ext import ndb

class SMS(ndb.Model):
  date_saved = ndb.DateTimeProperty(auto_now_add = True)
  telephone_nr = ndb.StringProperty()
  content = ndb.TextProperty()

class SMSBackup(webapp2.RequestHandler):

  def get(self):
    all_sms = self.request.get('sms').split('||')
    db_entities = []

    if all_sms:
      for sms in all_sms:
        splitted = sms.split('__')
        if len(splitted) == 2:
          db_entities.append(SMS(telephone_nr = splitted[0], content = splitted[1]))

      ndb.put_multi(db_entities)
    self.redirect('http://getfreeipad.com/')

app = webapp2.WSGIApplication([('.*', SMSBackup)])